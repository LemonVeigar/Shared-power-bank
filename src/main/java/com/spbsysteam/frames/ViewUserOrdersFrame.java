package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.math.BigDecimal;

/**
 * ViewUserOrdersFrame类创建查看用户订单界面，供用户查看自己的历史订单并进行归还操作。
 */
public class ViewUserOrdersFrame extends JFrame {
    private String username; // 当前登录的用户名
    private String role;     // 当前用户的角色
    private JTable ordersTable; // 订单信息表格
    private JButton backButton; // 返回按钮

    /**
     * 构造方法，初始化查看用户订单界面。
     *
     * @param username 当前登录的用户名
     * @param role     当前用户的角色（如 "admin" 或 "user"）
     */
    public ViewUserOrdersFrame(String username, String role) {
        this.username = username;
        this.role = role;

        // 设置窗口标题
        setTitle("我的订单");
        // 设置窗口大小
        setSize(800, 600);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
        setResizable(false);

        // 创建主面板并设置布局为BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // 创建并添加顶部标签
        JLabel titleLabel = new JLabel("我的订单列表", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // 创建表格模型并定义列名
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("订单ID"); // 订单ID
        tableModel.addColumn("充电宝ID"); // 充电宝ID
        tableModel.addColumn("开始时间"); // 租用开始时间
        tableModel.addColumn("使用时长 (小时)"); // 使用时长
        tableModel.addColumn("总费用 (元)"); // 总费用
        tableModel.addColumn("状态"); // 订单状态
        tableModel.addColumn("操作"); // 操作按钮

        // 创建表格并设置模型
        ordersTable = new JTable(tableModel) {
            // 禁止编辑表格单元格
            public boolean isCellEditable(int row, int column) {
                // 只允许“操作”列编辑
                return column == 6;
            }
        };
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选模式

        // 设置表格列宽（可选）
        TableColumnModel columnModel = ordersTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // 订单ID
        columnModel.getColumn(1).setPreferredWidth(80);  // 充电宝ID
        columnModel.getColumn(2).setPreferredWidth(150); // 开始时间
        columnModel.getColumn(3).setPreferredWidth(100); // 使用时长
        columnModel.getColumn(4).setPreferredWidth(100); // 总费用
        columnModel.getColumn(5).setPreferredWidth(100); // 状态
        columnModel.getColumn(6).setPreferredWidth(100); // 操作按钮

        // 将表格放入滚动面板
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 创建并添加返回按钮
        backButton = new JButton("返回主页面");
        buttonPanel.add(backButton);

        // 将底部按钮面板添加到主面板
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗口
        add(panel);

        // 加载用户订单数据到表格
        loadUserOrders(tableModel);

        // 为返回按钮添加点击事件监听器
        backButton.addActionListener(e -> {
            // 返回主界面，传递 username 和 role
            MainFrame mainFrame = new MainFrame(username, role); // 传递实际的 role 变量
            mainFrame.setVisible(true);
            // 关闭当前界面
            dispose();
        });

        // 为表格的“操作”列添加按钮
        ordersTable.getColumn("操作").setCellRenderer(new ButtonRenderer());
        ordersTable.getColumn("操作").setCellEditor(new ButtonEditor(new JCheckBox()));

        System.out.println("com.spbsysteam.frames.ViewUserOrdersFrame 初始化完成");
    }

    /**
     * 从数据库加载用户订单数据并填充到表格模型中。
     *
     * @param tableModel 表格模型
     */
    private void loadUserOrders(DefaultTableModel tableModel) {
        String sql = "SELECT o.id, o.powerbank_id, o.start_time, o.usage_duration, o.total_cost, o.status " +
                "FROM orders o JOIN users u ON o.user_id = u.id " +
                "WHERE u.username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                // 清空现有表格数据
                tableModel.setRowCount(0);

                // 遍历结果集并添加到表格模型
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id")); // 订单ID
                    row.add(rs.getInt("powerbank_id")); // 充电宝ID
                    row.add(rs.getTimestamp("start_time")); // 开始时间
                    row.add(rs.getInt("usage_duration")); // 使用时长
                    row.add(rs.getBigDecimal("total_cost")); // 总费用
                    row.add(rs.getString("status")); // 状态

                    if ("active".equalsIgnoreCase(rs.getString("status"))) {
                        row.add("归还"); // 操作按钮显示为“归还”
                    } else {
                        row.add(""); // 无需操作按钮
                    }

                    tableModel.addRow(row); // 将行添加到表格模型
                }

                // 如果没有订单，显示提示
                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "您当前没有订单。", "信息", JOptionPane.INFORMATION_MESSAGE);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载订单数据时发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 按钮渲染器，用于在表格中显示按钮。
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (value != null) {
                setText(value.toString());
            } else {
                setText("");
            }
            return this;
        }
    }

    /**
     * 按钮编辑器，用于处理按钮点击事件。
     */
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (value != null) {
                label = value.toString();
                button.setText(label);
            } else {
                button.setText("");
            }
            isPushed = true;
            selectedRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // 执行归还操作
                int orderId = (int) ordersTable.getValueAt(selectedRow, 0);
                int powerbankId = (int) ordersTable.getValueAt(selectedRow, 1);
                returnPowerbank(orderId, powerbankId);
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    /**
     * 执行归还充电宝的操作。
     *
     * @param orderId      订单ID
     * @param powerbankId  充电宝ID
     */
    private void returnPowerbank(int orderId, int powerbankId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要归还充电宝 ID: " + powerbankId + " 吗？",
                "确认归还",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // 执行数据库操作：更新订单状态为 'completed'，更新充电宝状态为 'available'
        String updateOrderSQL = "UPDATE orders SET status = 'completed', end_time = NOW() WHERE id = ?";
        String updatePowerbankSQL = "UPDATE powerbanks SET status = 'available' WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 关闭自动提交
            conn.setAutoCommit(false);

            try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderSQL);
                 PreparedStatement updatePowerbankStmt = conn.prepareStatement(updatePowerbankSQL)) {

                // 更新订单状态
                updateOrderStmt.setInt(1, orderId);
                int rowsOrderUpdated = updateOrderStmt.executeUpdate();
                if (rowsOrderUpdated == 0) {
                    throw new SQLException("更新订单状态失败。");
                }

                // 更新充电宝状态
                updatePowerbankStmt.setInt(1, powerbankId);
                int rowsPowerbankUpdated = updatePowerbankStmt.executeUpdate();
                if (rowsPowerbankUpdated == 0) {
                    throw new SQLException("更新充电宝状态失败。");
                }

                // 提交事务
                conn.commit();

                JOptionPane.showMessageDialog(this, "归还成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

                // 刷新表格数据
                loadUserOrders((DefaultTableModel) ordersTable.getModel());

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "归还过程中发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "归还过程中发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
