package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.math.BigDecimal;

/**
 * ViewAvailablePowerbanksFrame类创建查看可用充电宝界面，供用户查看所有可用的充电宝。
 */
public class ViewAvailablePowerbanksFrame extends JFrame {
    private String username; // 当前登录的用户名
    private String role;     // 当前用户的角色
    private JTable powerbankTable; // 充电宝信息表格
    private JButton backButton; // 返回按钮
    private JButton rentButton; // 租用按钮

    /**
     * 构造方法，初始化查看可用充电宝界面。
     *
     * @param username 当前登录的用户名
     * @param role     当前用户的角色（如 "admin" 或 "user"）
     */
    public ViewAvailablePowerbanksFrame(String username, String role) {
        this.username = username;
        this.role = role;

        // 调试输出，确认传递的参数
        System.out.println("ViewAvailablePowerbanksFrame 构造器调用，username: " + username + ", role: " + role);

        // 设置窗口标题
        setTitle("查看可用充电宝");
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
        JLabel titleLabel = new JLabel("可用充电宝列表", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // 创建表格模型并定义列名
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID"); // 充电宝ID
        tableModel.addColumn("位置"); // 位置
        tableModel.addColumn("电量 (%)"); // 电量
        tableModel.addColumn("状态"); // 状态
        tableModel.addColumn("租赁价格 (元/小时)"); // 租赁价格

        // 创建表格并设置模型
        powerbankTable = new JTable(tableModel) {
            // 禁止编辑表格单元格
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        powerbankTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选模式

        // 设置表格列宽（可选）
        powerbankTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        powerbankTable.getColumnModel().getColumn(1).setPreferredWidth(150); // 位置
        powerbankTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 电量
        powerbankTable.getColumnModel().getColumn(3).setPreferredWidth(100); // 状态
        powerbankTable.getColumnModel().getColumn(4).setPreferredWidth(150); // 租赁价格

        // 将表格放入滚动面板
        JScrollPane scrollPane = new JScrollPane(powerbankTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 创建并添加返回按钮
        backButton = new JButton("返回主页面");
        buttonPanel.add(backButton);

        // 创建并添加租用按钮
        rentButton = new JButton("租用");
        buttonPanel.add(rentButton);

        // 根据角色控制“租用”按钮的可见性
        if (!"user".equalsIgnoreCase(role)) {
            rentButton.setVisible(false); // 仅普通用户可见
        }

        // 将底部按钮面板添加到主面板
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗口
        add(panel);

        // 加载可用充电宝数据到表格
        loadAvailablePowerbanks(tableModel);

        // 为返回按钮添加点击事件监听器
        backButton.addActionListener(e -> {
            // 返回主界面，传递 username 和 role
            MainFrame mainFrame = new MainFrame(username, role);
            mainFrame.setVisible(true);
            // 关闭当前界面
            dispose();
        });

        // 为租用按钮添加点击事件监听器
        rentButton.addActionListener(e -> {
            // 获取选中的行
            int selectedRow = powerbankTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请选择要租用的充电宝。", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 获取充电宝ID
            int powerbankId = (int) powerbankTable.getValueAt(selectedRow, 0);
            String location = (String) powerbankTable.getValueAt(selectedRow, 1);
            int batteryLevel = (int) powerbankTable.getValueAt(selectedRow, 2);
            String status = (String) powerbankTable.getValueAt(selectedRow, 3);
            BigDecimal pricePerHour = (BigDecimal) powerbankTable.getValueAt(selectedRow, 4);

            // 确认租用
            int confirm = JOptionPane.showConfirmDialog(this,
                    "确定要租用充电宝 ID: " + powerbankId + "，位置: " + location + " 吗？",
                    "确认租用",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // 执行租用操作
            rentPowerbank(powerbankId);
        });

        System.out.println("com.spbsysteam.frames.ViewAvailablePowerbanksFrame 初始化完成");
    }

    /**
     * 从数据库加载可用充电宝数据并填充到表格模型中。
     *
     * @param tableModel 表格模型
     */
    private void loadAvailablePowerbanks(DefaultTableModel tableModel) {
        String sql = "SELECT id, location, battery_level, status, price_per_hour FROM powerbanks WHERE status = 'available'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 清空现有表格数据
            tableModel.setRowCount(0);

            // 遍历结果集并添加到表格模型
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id")); // 充电宝ID
                row.add(rs.getString("location")); // 位置
                row.add(rs.getInt("battery_level")); // 电量
                row.add(rs.getString("status")); // 状态
                row.add(rs.getBigDecimal("price_per_hour")); // 租赁价格
                tableModel.addRow(row); // 将行添加到表格模型
            }

            // 如果没有可用充电宝，显示提示
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "当前没有可用的充电宝。", "信息", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载可用充电宝数据时发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 执行租用充电宝的操作。
     *
     * @param powerbankId 要租用的充电宝ID
     */
    private void rentPowerbank(int powerbankId) {
        // 获取当前时间作为租用开始时间
        Timestamp startTime = new Timestamp(System.currentTimeMillis());

        // 设定租用时长（例如，用户输入租用时长）
        String durationStr = JOptionPane.showInputDialog(this, "请输入租用时长（小时）：", "租用时长", JOptionPane.QUESTION_MESSAGE);
        if (durationStr == null || durationStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "租用时长不能为空。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int usageDuration;
        try {
            usageDuration = Integer.parseInt(durationStr.trim());
            if (usageDuration <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的租用时长（正整数）。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 获取租赁价格，确保使用正确的行索引
        int selectedRow = powerbankTable.getSelectedRow();
        BigDecimal pricePerHour;
        try {
            pricePerHour = (BigDecimal) powerbankTable.getValueAt(selectedRow, 4);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "获取租赁价格失败。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        BigDecimal totalCost = pricePerHour.multiply(new BigDecimal(usageDuration));

        // 获取用户ID
        int userId = -1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            userId = getUserId(conn, username);
            if (userId == -1) {
                JOptionPane.showMessageDialog(this, "获取用户ID失败。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取用户ID时发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 调试输出
        System.out.println("准备插入订单：userId=" + userId + ", powerbankId=" + powerbankId + ", startTime=" + startTime + ", usageDuration=" + usageDuration + ", totalCost=" + totalCost + ", status='active'");

        // 执行数据库操作：插入订单，更新充电宝状态
        String insertOrderSQL = "INSERT INTO orders (user_id, powerbank_id, start_time, usage_duration, total_cost, status) VALUES (?, ?, ?, ?, ?, ?)";
        String updatePowerbankSQL = "UPDATE powerbanks SET status = 'rented' WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 关闭自动提交
            conn.setAutoCommit(false);

            try (PreparedStatement insertOrderStmt = conn.prepareStatement(insertOrderSQL);
                 PreparedStatement updatePowerbankStmt = conn.prepareStatement(updatePowerbankSQL)) {

                // 插入订单
                insertOrderStmt.setInt(1, userId);
                insertOrderStmt.setInt(2, powerbankId);
                insertOrderStmt.setTimestamp(3, startTime);
                insertOrderStmt.setInt(4, usageDuration);
                insertOrderStmt.setBigDecimal(5, totalCost);
                insertOrderStmt.setString(6, "active"); // 设置 status

                int rowsInserted = insertOrderStmt.executeUpdate();
                if (rowsInserted == 0) {
                    throw new SQLException("插入订单失败。");
                }

                // 更新充电宝状态
                updatePowerbankStmt.setInt(1, powerbankId);
                int rowsUpdated = updatePowerbankStmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new SQLException("更新充电宝状态失败。");
                }

                // 提交事务
                conn.commit();

                JOptionPane.showMessageDialog(this, "租用成功！总费用：" + totalCost + " 元。", "成功", JOptionPane.INFORMATION_MESSAGE);

                // 刷新表格数据
                loadAvailablePowerbanks((DefaultTableModel) powerbankTable.getModel());

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "租用过程中发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "租用过程中发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 根据用户名获取用户ID。
     *
     * @param conn     数据库连接
     * @param username 用户名
     * @return 用户ID，如果未找到返回-1
     */
    private int getUserId(Connection conn, String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    System.out.println("获取到的 userId: " + userId);
                    return userId;
                } else {
                    return -1;
                }
            }
        }
    }
}
