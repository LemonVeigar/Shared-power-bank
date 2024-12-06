package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/**
 * PowerbankListFrame类创建充电宝列表界面，供用户查看和租赁充电宝。
 */
public class PowerbankListFrame extends JFrame {
    private String username; // 当前登录的用户名
    private JTable powerbankTable; // 充电宝信息表格
    private JButton rentButton; // 租赁按钮
    private JButton backButton; // 返回按钮

    /**
     * 构造方法，初始化充电宝列表界面。
     *
     * @param username 当前登录的用户名
     */
    public PowerbankListFrame(String username) {
        this.username = username; // 保存用户名

        // 设置窗口标题
        setTitle("共享充电宝租赁系统 - 可用充电宝列表");
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
        JLabel titleLabel = new JLabel("可用充电宝列表", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24)); // 使用支持中文的字体
        panel.add(titleLabel, BorderLayout.NORTH);

        // 创建表格模型并定义列名
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID"); // 充电宝ID
        tableModel.addColumn("位置"); // 充电宝位置
        tableModel.addColumn("纬度"); // 充电宝纬度
        tableModel.addColumn("经度"); // 充电宝经度
        tableModel.addColumn("剩余电量 (%)"); // 剩余电量
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

        // 设置表格列宽
        powerbankTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        powerbankTable.getColumnModel().getColumn(1).setPreferredWidth(150); // 位置
        powerbankTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 纬度
        powerbankTable.getColumnModel().getColumn(3).setPreferredWidth(100); // 经度
        powerbankTable.getColumnModel().getColumn(4).setPreferredWidth(100); // 剩余电量
        powerbankTable.getColumnModel().getColumn(5).setPreferredWidth(100); // 状态
        powerbankTable.getColumnModel().getColumn(6).setPreferredWidth(150); // 租赁价格

        // 将表格放入滚动面板
        JScrollPane scrollPane = new JScrollPane(powerbankTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板并设置布局为FlowLayout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 创建并添加租赁按钮
        rentButton = new JButton("租赁选中充电宝");
        buttonPanel.add(rentButton);

        // 创建并添加返回按钮
        backButton = new JButton("返回主界面");
        buttonPanel.add(backButton);

        // 将按钮面板添加到主面板南部
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗口
        add(panel);

        // 加载充电宝数据到表格
        loadPowerbankData(tableModel);

        // 为租赁按钮添加点击事件监听器
        rentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 处理租赁逻辑
                rentSelectedPowerbank();
            }
        });

        // 为返回按钮添加点击事件监听器
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 返回主界面
                goBackToMainFrame();
            }
        });
    }

    /**
     * 从数据库加载可用充电宝数据并填充到表格模型中。
     *
     * @param tableModel 表格模型
     */
    private void loadPowerbankData(DefaultTableModel tableModel) {
        // 使用try-with-resources确保连接和语句在使用后自动关闭
        String sql = "SELECT * FROM powerbanks WHERE status = 'available' AND battery_level > 50";

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
                row.add(rs.getInt("id")); // 添加ID
                row.add(rs.getString("location")); // 添加位置
                row.add(rs.getDouble("latitude")); // 添加纬度
                row.add(rs.getDouble("longitude")); // 添加经度
                row.add(rs.getInt("battery_level")); // 添加剩余电量
                row.add(rs.getString("status")); // 添加状态
                row.add(rs.getBigDecimal("price_per_hour")); // 添加租赁价格
                tableModel.addRow(row); // 将行添加到表格模型
            }

        } catch (SQLException e) {
            // 捕捉并显示SQL异常
            e.printStackTrace(); // 打印堆栈跟踪到控制台
            JOptionPane.showMessageDialog(this, "加载充电宝数据时发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE); // 显示详细错误消息
        }
    }

    /**
     * 处理租赁选中充电宝的逻辑。
     */
    private void rentSelectedPowerbank() {
        int selectedRow = powerbankTable.getSelectedRow(); // 获取选中的行
        if (selectedRow == -1) { // 如果未选择任何行
            JOptionPane.showMessageDialog(this, "请选择要租赁的充电宝", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取选中充电宝的ID和租赁价格
        int powerbankId = (int) powerbankTable.getValueAt(selectedRow, 0); // 获取ID
        double pricePerHour = ((Number) powerbankTable.getValueAt(selectedRow, 6)).doubleValue(); // 获取租赁价格

        // 弹出对话框输入租赁时长
        String durationStr = JOptionPane.showInputDialog(this, "请输入租赁时长（小时）：", "租赁充电宝", JOptionPane.PLAIN_MESSAGE);
        if (durationStr == null) { // 用户取消输入
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr); // 解析输入为整数
            if (duration <= 0) { // 验证时长为正整数
                throw new NumberFormatException("时长必须为正整数");
            }
        } catch (NumberFormatException e) { // 输入无效
            JOptionPane.showMessageDialog(this, "请输入有效的租赁时长（正整数）", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 计算总费用
        double totalCost = duration * 1.5; // 每小时1.5元

        // 使用try-with-resources确保连接和语句在使用后自动关闭
        String getUserIdSql = "SELECT id FROM users WHERE username = ?";
        String insertOrderSql = "INSERT INTO orders (user_id, powerbank_id, start_time, usage_duration, total_cost, status) VALUES (?, ?, NOW(), ?, ?, 'active')";
        String updatePowerbankSql = "UPDATE powerbanks SET status = 'rented' WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 开始事务
            conn.setAutoCommit(false);

            // 获取用户ID
            try (PreparedStatement getUserIdStmt = conn.prepareStatement(getUserIdSql)) {
                getUserIdStmt.setString(1, username); // 设置用户名参数
                try (ResultSet userRs = getUserIdStmt.executeQuery()) {
                    if (!userRs.next()) { // 如果未找到用户
                        JOptionPane.showMessageDialog(this, "未找到用户信息", "错误", JOptionPane.ERROR_MESSAGE);
                        System.out.println("未找到用户名：" + username);
                        conn.rollback(); // 回滚事务
                        return;
                    }

                    int userId = userRs.getInt("id"); // 获取用户ID
                    System.out.println("获取到的用户ID：" + userId);

                    // 插入订单记录
                    try (PreparedStatement insertOrderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                        insertOrderStmt.setInt(1, userId); // 设置用户ID
                        insertOrderStmt.setInt(2, powerbankId); // 设置充电宝ID
                        insertOrderStmt.setInt(3, duration); // 设置使用时长
                        insertOrderStmt.setDouble(4, totalCost); // 设置总费用
                        int rowsInserted = insertOrderStmt.executeUpdate(); // 执行插入
                        System.out.println("订单插入结果行数：" + rowsInserted);

                        if (rowsInserted == 0) { // 如果插入失败
                            throw new SQLException("创建订单失败");
                        }

                        // 获取生成的订单ID（可选）
                        int orderId = -1;
                        try (ResultSet generatedKeys = insertOrderStmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                orderId = generatedKeys.getInt(1); // 获取订单ID
                                System.out.println("生成的订单ID：" + orderId);
                            }
                        }

                        // 更新充电宝状态为 'rented'
                        try (PreparedStatement updatePowerbankStmt = conn.prepareStatement(updatePowerbankSql)) {
                            updatePowerbankStmt.setInt(1, powerbankId); // 设置充电宝ID
                            int rowsUpdated = updatePowerbankStmt.executeUpdate(); // 执行更新
                            System.out.println("充电宝状态更新结果行数：" + rowsUpdated);

                            if (rowsUpdated == 0) { // 如果更新失败
                                throw new SQLException("更新充电宝状态失败");
                            }
                        }

                        // 提交事务
                        conn.commit();
                        System.out.println("事务已提交");

                        // 提示租赁成功
                        JOptionPane.showMessageDialog(this, "租赁成功！订单ID：" + orderId + "\n总费用：" + totalCost + " 元", "成功", JOptionPane.INFORMATION_MESSAGE);

                        // 刷新充电宝列表
                        loadPowerbankData((DefaultTableModel) powerbankTable.getModel());

                    }
                }
            }

        } catch (SQLException e) { // 捕捉并处理SQL异常
            e.printStackTrace(); // 打印堆栈跟踪到控制台
            // 显示详细错误消息
            JOptionPane.showMessageDialog(this, "租赁过程中发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 返回主界面的方法。
     */
    private void goBackToMainFrame() {
        // 创建并显示主界面
        MainFrame mainFrame = new MainFrame(username);
        mainFrame.setVisible(true);
        System.out.println("返回主界面");
        // 关闭当前充电宝列表界面
        this.dispose();
    }
}
