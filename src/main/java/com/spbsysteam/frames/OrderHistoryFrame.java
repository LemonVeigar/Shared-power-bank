package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

/**
 * OrderHistoryFrame类创建用户历史订单信息查询界面，供用户查看个人的历史订单。
 */
public class OrderHistoryFrame extends JFrame {
    private String username; // 当前登录的用户名
    private JTable orderTable; // 订单信息表格
    private JButton backButton; // 返回按钮

    /**
     * 构造方法，初始化历史订单查询界面。
     *
     * @param username 当前登录的用户名
     */
    public OrderHistoryFrame(String username) {
        this.username = username; // 保存用户名

        // 设置窗口标题
        setTitle("历史订单信息");
        // 设置窗口大小
        setSize(800, 600);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
        setResizable(false);

        // 创建主面板并设置布局为BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // 创建并添加顶部标签
        JLabel titleLabel = new JLabel("历史订单信息", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // 创建表格模型并定义列名
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("订单ID"); // 订单ID
        tableModel.addColumn("充电宝ID"); // 充电宝ID
        tableModel.addColumn("开始时间"); // 开始时间
        tableModel.addColumn("使用时长 (小时)"); // 使用时长
        tableModel.addColumn("总费用 (元)"); // 总费用
        tableModel.addColumn("状态"); // 状态

        // 创建表格并设置模型
        orderTable = new JTable(tableModel) {
            // 禁止编辑表格单元格
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选模式

        // 设置表格列宽
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // 订单ID
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(80); // 充电宝ID
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(150); // 开始时间
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100); // 使用时长
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(100); // 总费用
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(100); // 状态

        // 将表格放入滚动面板
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建底部按钮面板
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 创建并添加返回按钮
        backButton = new JButton("返回主界面");
        bottomPanel.add(backButton);

        // 将底部按钮面板添加到主面板
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗口
        add(panel);

        // 加载订单数据到表格
        loadOrderData(tableModel);

        // 为返回按钮添加点击事件监听器
        backButton.addActionListener(e -> {
            // 返回主界面
            MainFrame mainFrame = new MainFrame(username); // 确保传递用户名
            mainFrame.setVisible(true);
            // 关闭当前界面
            dispose();
        });
    }

    /**
     * 从数据库加载用户的历史订单数据并填充到表格模型中。
     *
     * @param tableModel 表格模型
     */
    private void loadOrderData(DefaultTableModel tableModel) {
        String sql = "SELECT o.id AS order_id, o.powerbank_id, o.start_time, o.usage_duration, o.total_cost, o.status " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "WHERE u.username = ? " +
                "ORDER BY o.start_time DESC";

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
                    row.add(rs.getInt("order_id")); // 订单ID
                    row.add(rs.getInt("powerbank_id")); // 充电宝ID
                    row.add(rs.getTimestamp("start_time")); // 开始时间
                    row.add(rs.getInt("usage_duration")); // 使用时长
                    row.add(rs.getBigDecimal("total_cost")); // 总费用
                    row.add(rs.getString("status")); // 状态
                    tableModel.addRow(row); // 将行添加到表格模型
                }

                // 如果没有订单记录，显示提示
                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "您没有历史订单记录", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载订单数据时发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
