import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * RegisterFrame类创建用户注册界面。
 */
public class RegisterFrame extends JFrame {
    // 声明界面组件
    private JTextField usernameField; // 用户名输入框
    private JPasswordField passwordField; // 密码输入框
    private JPasswordField confirmPasswordField; // 确认密码输入框
    private JTextField phoneField; // 手机号输入框
    private JButton registerButton; // 注册按钮
    private JButton backButton; // 返回登录按钮

    /**
     * 构造方法，初始化注册界面。
     */
    public RegisterFrame() {
        // 设置窗口标题
        setTitle("共享充电宝租赁系统 - 注册");
        // 设置窗口大小
        setSize(400, 300);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
        setResizable(false);

        // 创建面板并设置布局
        JPanel panel = new JPanel();
        panel.setLayout(null); // 使用绝对布局

        // 创建并添加用户名标签
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setBounds(50, 40, 100, 25); // 设置位置和大小
        panel.add(userLabel);

        // 创建并添加用户名输入框
        usernameField = new JTextField(20);
        usernameField.setBounds(160, 40, 180, 25); // 设置位置和大小
        panel.add(usernameField);

        // 创建并添加密码标签
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setBounds(50, 80, 100, 25); // 设置位置和大小
        panel.add(passwordLabel);

        // 创建并添加密码输入框
        passwordField = new JPasswordField(20);
        passwordField.setBounds(160, 80, 180, 25); // 设置位置和大小
        panel.add(passwordField);

        // 创建并添加确认密码标签
        JLabel confirmPasswordLabel = new JLabel("确认密码:");
        confirmPasswordLabel.setBounds(50, 120, 100, 25); // 设置位置和大小
        panel.add(confirmPasswordLabel);

        // 创建并添加确认密码输入框
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBounds(160, 120, 180, 25); // 设置位置和大小
        panel.add(confirmPasswordField);

        // 创建并添加手机号标签
        JLabel phoneLabel = new JLabel("手机号:");
        phoneLabel.setBounds(50, 160, 100, 25); // 设置位置和大小
        panel.add(phoneLabel);

        // 创建并添加手机号输入框
        phoneField = new JTextField(20);
        phoneField.setBounds(160, 160, 180, 25); // 设置位置和大小
        panel.add(phoneField);

        // 创建并添加注册按钮
        registerButton = new JButton("注册");
        registerButton.setBounds(50, 210, 100, 30); // 设置位置和大小
        panel.add(registerButton);

        // 创建并添加返回登录按钮
        backButton = new JButton("返回登录");
        backButton.setBounds(240, 210, 100, 30); // 设置位置和大小
        panel.add(backButton);

        // 将面板添加到窗口
        add(panel);

        // 为注册按钮添加点击事件监听器
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用注册方法
                register();
            }
        });

        // 为返回登录按钮添加点击事件监听器
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 打开登录界面
                openLoginFrame();
            }
        });
    }

    /**
     * 注册方法，处理用户注册逻辑。
     */
    private void register() {
        // 获取用户输入
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String phone = phoneField.getText();

        // 简单验证输入是否为空
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 验证密码是否一致
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 获取数据库连接
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 准备插入用户的SQL语句
            String sql = "INSERT INTO users (username, password, phone) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username); // 设置用户名参数
            pstmt.setString(2, password); // 设置密码参数（注意：实际项目中应使用密码哈希）
            pstmt.setString(3, phone.isEmpty() ? null : phone); // 设置手机号参数，如果为空则为null

            // 执行插入操作
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // 注册成功，显示成功信息
                JOptionPane.showMessageDialog(this, "注册成功！请登录", "成功", JOptionPane.INFORMATION_MESSAGE);
                // 打开登录界面
                openLoginFrame();
            } else {
                // 注册失败，显示错误信息
                JOptionPane.showMessageDialog(this, "注册失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            }

            // 关闭预处理语句和数据库连接
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            // 捕捉并处理SQL异常（如用户名重复）
            if (ex.getErrorCode() == 1062) { // 1062是MySQL的重复键错误码
                JOptionPane.showMessageDialog(this, "用户名已存在，请选择其他用户名", "错误", JOptionPane.ERROR_MESSAGE);
            } else {
                // 打印异常堆栈信息
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "注册过程中发生错误", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 打开登录界面的方法。
     */
    private void openLoginFrame() {
        // 创建登录界面实例
        LoginFrame loginFrame = new LoginFrame();
        // 设置登录界面可见
        loginFrame.setVisible(true);
        // 关闭当前注册界面
        this.dispose();
    }
}
