import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * UserProfileFrame类创建用户信息管理界面，供用户查看和修改个人信息。
 */
public class UserProfileFrame extends JFrame {
    private String username; // 当前登录的用户名
    private JTextField phoneField; // 手机号输入框
    private JButton updateButton; // 更新按钮
    private JButton backButton; // 返回按钮

    /**
     * 构造方法，初始化用户信息管理界面。
     *
     * @param username 当前登录的用户名
     */
    public UserProfileFrame(String username) {
        this.username = username;

        // 设置窗口标题
        setTitle("共享充电宝租赁系统 - 用户信息管理");
        // 设置窗口大小
        setSize(400, 300);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
        setResizable(false);

        // 创建主面板并设置布局
        JPanel panel = new JPanel();
        panel.setLayout(null); // 使用绝对布局

        // 创建并添加用户名标签
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setBounds(50, 50, 80, 25);
        panel.add(userLabel);

        JTextField usernameField = new JTextField(username);
        usernameField.setBounds(150, 50, 180, 25);
        usernameField.setEditable(false); // 禁止编辑用户名
        panel.add(usernameField);

        // 创建并添加手机号标签
        JLabel phoneLabel = new JLabel("手机号:");
        phoneLabel.setBounds(50, 90, 80, 25);
        panel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(150, 90, 180, 25);
        panel.add(phoneField);

        // 创建并添加更新按钮
        updateButton = new JButton("更新");
        updateButton.setBounds(50, 150, 100, 30);
        panel.add(updateButton);

        // 创建并添加返回按钮
        backButton = new JButton("返回");
        backButton.setBounds(230, 150, 100, 30);
        panel.add(backButton);

        // 将主面板添加到窗口
        add(panel);

        // 加载用户当前的手机号
        loadUserProfile();

        // 为更新按钮添加点击事件监听器
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 处理更新逻辑
                updateUserProfile();
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
     * 从数据库加载当前用户的个人信息。
     */
    private void loadUserProfile() {
        // 获取数据库连接
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 准备查询用户信息的SQL语句
            String sql = "SELECT phone FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);

            // 执行查询
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String phone = rs.getString("phone");
                phoneField.setText(phone != null ? phone : "");
            }

            // 关闭结果集和预处理语句
            rs.close();
            pstmt.close();
            // 关闭数据库连接
            conn.close();
        } catch (SQLException e) {
            // 捕捉并显示SQL异常
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载用户信息时发生错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 处理更新用户个人信息的逻辑。
     */
    private void updateUserProfile() {
        String newPhone = phoneField.getText().trim();

        // 可以添加手机号格式验证
        if (!newPhone.matches("\\d{10,15}")) { // 简单的手机号验证
            JOptionPane.showMessageDialog(this, "请输入有效的手机号（10-15位数字）", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 获取数据库连接
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 准备更新用户信息的SQL语句
            String sql = "UPDATE users SET phone = ? WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPhone.isEmpty() ? null : newPhone);
            pstmt.setString(2, username);

            // 执行更新
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "个人信息更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "更新失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            }

            // 关闭预处理语句和数据库连接
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            // 捕捉并处理SQL异常
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "更新个人信息时发生错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 返回主界面的方法。
     */
    private void goBackToMainFrame() {
        // 创建并显示主界面
        MainFrame mainFrame = new MainFrame(username);
        mainFrame.setVisible(true);
        // 关闭当前用户信息管理界面
        this.dispose();
    }
}
