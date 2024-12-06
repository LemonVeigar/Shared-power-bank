import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/**
 * OrderHistoryFrame类创建租借记录界面，供用户查看和归还充电宝。
 */
public class OrderHistoryFrame extends JFrame {
    private String username; // 当前登录的用户名
    private JTable orderTable; // 租借记录表格
    private JButton returnButton; // 归还按钮
    private JButton backButton; // 返回按钮

    /**
     * 构造方法，初始化租借记录界面。
     *
     * @param username 当前登录的用户名
     */
    public OrderHistoryFrame(String username) {
        this.username = username; // 保存用户名

        // 设置窗口标题
        setTitle("共享充电宝租赁系统 - 租借记录");
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
        JLabel titleLabel = new JLabel("租借记录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24)); // 使用支持中文的字体
        panel.add(titleLabel, BorderLayout.NORTH);

        // 创建表格模型并定义列名
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("订单ID"); // 订单ID
        tableModel.addColumn("充电宝ID"); // 充电宝ID
        tableModel.addColumn("租赁开始时间"); // 租赁开始时间
        tableModel.addColumn("租赁时长 (小时)"); // 租赁时长
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
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // 订单ID
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(100); // 充电宝ID
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(200); // 租赁开始时间
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100); // 租赁时长
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(100); // 总费用
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(100); // 状态

        // 将表格放入滚动面板
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板并设置布局为FlowLayout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 创建并添加归还按钮
        returnButton = new JButton("归还选中充电宝");
        buttonPanel.add(returnButton);

        // 创建并添加返回按钮
        backButton = new JButton("返回主界面");
        buttonPanel.add(backButton);

        // 将按钮面板添加到主面板南部
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗口
        add(panel);

        // 加载租借记录数据到表格
        loadOrderData(tableModel);

        // 为归还按钮添加点击事件监听器
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 处理归还逻辑
                returnSelectedPowerbank();
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
     * 从数据库加载租借记录数据并填充到表格模型中。
     *
     * @param tableModel 表格模型
     */
    private void loadOrderData(DefaultTableModel tableModel) {
        String sql = "SELECT * FROM orders WHERE user_id = (SELECT id FROM users WHERE username = ?)";

        // 使用try-with-resources确保连接和语句在使用后自动关闭
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            pstmt.setString(1, username); // 设置用户名参数

            try (ResultSet rs = pstmt.executeQuery()) {
                // 清空现有表格数据
                tableModel.setRowCount(0);

                // 遍历结果集并添加到表格模型
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id")); // 添加订单ID
                    row.add(rs.getInt("powerbank_id")); // 添加充电宝ID
                    row.add(rs.getTimestamp("start_time")); // 添加租赁开始时间
                    row.add(rs.getInt("usage_duration")); // 添加租赁时长
                    row.add(rs.getDouble("total_cost")); // 添加总费用
                    row.add(rs.getString("status")); // 添加状态
                    tableModel.addRow(row); // 将行添加到表格模型
                }
            }

        } catch (SQLException e) {
            // 捕捉并显示SQL异常
            e.printStackTrace(); // 打印堆栈跟踪到控制台
            JOptionPane.showMessageDialog(this, "加载租借记录数据时发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 处理归还选中充电宝的逻辑。
     */
    private void returnSelectedPowerbank() {
        int selectedRow = orderTable.getSelectedRow(); // 获取选中的行
        if (selectedRow == -1) { // 如果未选择任何行
            JOptionPane.showMessageDialog(this, "请选择要归还的充电宝订单", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取选中订单的相关信息
        int orderId = (int) orderTable.getValueAt(selectedRow, 0); // 获取订单ID
        int powerbankId = (int) orderTable.getValueAt(selectedRow, 1); // 获取充电宝ID
        String orderStatus = (String) orderTable.getValueAt(selectedRow, 5); // 获取订单状态

        if (!"active".equalsIgnoreCase(orderStatus)) { // 仅允许归还活跃订单
            JOptionPane.showMessageDialog(this, "仅可归还正在租赁中的充电宝", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 弹出确认对话框
        int confirm = JOptionPane.showConfirmDialog(this, "确定要归还充电宝ID：" + powerbankId + " 吗？", "确认归还", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return; // 用户选择否，取消操作
        }

        // 使用try-with-resources确保连接和语句在使用后自动关闭
        String getUserIdSql = "SELECT id FROM users WHERE username = ?";
        String updateOrderSql = "UPDATE orders SET status = 'completed', end_time = NOW() WHERE id = ?";
        String updatePowerbankSql = "UPDATE powerbanks SET status = 'available', battery_level = 100 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 开始事务
            conn.setAutoCommit(false);

            // 获取用户ID（可选，如果需要的话）
            /*
            try (PreparedStatement getUserIdStmt = conn.prepareStatement(getUserIdSql)) {
                getUserIdStmt.setString(1, username);
                try (ResultSet userRs = getUserIdStmt.executeQuery()) {
                    if (!userRs.next()) {
                        JOptionPane.showMessageDialog(this, "未找到用户信息", "错误", JOptionPane.ERROR_MESSAGE);
                        conn.rollback();
                        return;
                    }
                    int userId = userRs.getInt("id");
                    // 如果需要使用userId，可以在这里处理
                }
            }
            */

            // 更新订单状态为 'completed'
            try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderSql)) {
                updateOrderStmt.setInt(1, orderId); // 设置订单ID参数
                int rowsUpdated = updateOrderStmt.executeUpdate(); // 执行更新
                System.out.println("订单状态更新结果行数：" + rowsUpdated);

                if (rowsUpdated == 0) { // 如果更新失败
                    throw new SQLException("更新订单状态失败");
                }
            }

            // 更新充电宝状态为 'available' 并将电量恢复为100%
            try (PreparedStatement updatePowerbankStmt = conn.prepareStatement(updatePowerbankSql)) {
                updatePowerbankStmt.setInt(1, powerbankId); // 设置充电宝ID参数
                int rowsUpdated = updatePowerbankStmt.executeUpdate(); // 执行更新
                System.out.println("充电宝状态更新结果行数：" + rowsUpdated);

                if (rowsUpdated == 0) { // 如果更新失败
                    throw new SQLException("更新充电宝状态失败");
                }
            }

            // 提交事务
            conn.commit();
            System.out.println("事务已提交");

            // 提示归还成功
            JOptionPane.showMessageDialog(this, "充电宝归还成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

            // 刷新租借记录列表
            DefaultTableModel tableModel = (DefaultTableModel) orderTable.getModel();
            loadOrderData(tableModel);

        } catch (SQLException e) { // 捕捉并处理SQL异常
            e.printStackTrace(); // 打印堆栈跟踪到控制台
            try {
                // 尝试回滚事务
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null) {
                    conn.rollback();
                    System.out.println("事务已回滚");
                }
            } catch (SQLException rollbackEx) { // 处理回滚异常
                rollbackEx.printStackTrace(); // 打印堆栈跟踪到控制台
            }
            // 显示详细错误消息
            JOptionPane.showMessageDialog(this, "归还过程中发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                // 确保连接恢复自动提交
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) { // 处理设置自动提交异常
                e.printStackTrace(); // 打印堆栈跟踪到控制台
            }
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
        // 关闭当前租借记录界面
        this.dispose();
    }
}
