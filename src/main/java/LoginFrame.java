import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * LoginFrame类创建用户登录界面。
 */
public class LoginFrame extends JFrame {
    // 声明界面组件
    private JTextField usernameField; // 用户名输入框
    private JPasswordField passwordField; // 密码输入框
    private JButton loginButton; // 登录按钮
    private JButton registerButton; // 注册按钮

    /**
     * 构造方法，初始化登录界面。
     */
    public LoginFrame() {
        // 设置窗口标题
        setTitle("共享充电宝租赁系统 - 登录");
        // 设置窗口大小
        setSize(400, 250);
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
        userLabel.setBounds(50, 50, 80, 25); // 设置位置和大小
        panel.add(userLabel);

        // 创建并添加用户名输入框
        usernameField = new JTextField(20);
        usernameField.setBounds(150, 50, 180, 25); // 设置位置和大小
        panel.add(usernameField);

        // 创建并添加密码标签
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setBounds(50, 90, 80, 25); // 设置位置和大小
        panel.add(passwordLabel);

        // 创建并添加密码输入框
        passwordField = new JPasswordField(20);
        passwordField.setBounds(150, 90, 180, 25); // 设置位置和大小
        panel.add(passwordField);

        // 创建并添加登录按钮
        loginButton = new JButton("登录");
        loginButton.setBounds(50, 150, 100, 30); // 设置位置和大小
        panel.add(loginButton);

        // 创建并添加注册按钮
        registerButton = new JButton("注册");
        registerButton.setBounds(230, 150, 100, 30); // 设置位置和大小
        panel.add(registerButton);

        // 将面板添加到窗口
        add(panel);

        // 为登录按钮添加点击事件监听器
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用登录方法
                login();
            }
        });

        // 为注册按钮添加点击事件监听器
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 打开注册界面
                openRegisterFrame();
            }
        });
    }

    /**
     * 登录方法，验证用户输入的用户名和密码。
     */
    private void login() {
        // 获取用户名和密码输入
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // 简单验证输入是否为空
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 获取数据库连接
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 准备SQL语句，查询用户是否存在
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username); // 设置用户名参数
            pstmt.setString(2, password); // 设置密码参数（注意：实际项目中应使用密码哈希）

            // 执行查询
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // 登录成功，显示欢迎信息
                JOptionPane.showMessageDialog(this, "登录成功！欢迎 " + username, "成功", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("登录成功，正在打开 MainFrame...");

                // 打开主界面
                MainFrame mainFrame = new MainFrame(username);
                mainFrame.setVisible(true);
                System.out.println("MainFrame 已打开");

                // 关闭登录界面
                this.dispose();
                System.out.println("登录界面已关闭");
            } else {
                // 登录失败，显示错误信息
                JOptionPane.showMessageDialog(this, "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
            }

            // 关闭结果集和预处理语句
            rs.close();
            pstmt.close();
            // 关闭数据库连接
            conn.close();
        } catch (Exception ex) {
            // 捕捉并显示异常信息
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "登录过程中发生错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 打开注册界面的方法。
     */
    private void openRegisterFrame() {
        // 创建注册界面实例
        RegisterFrame registerFrame = new RegisterFrame();
        // 设置注册界面可见
        registerFrame.setVisible(true);
        // 关闭当前登录界面
        this.dispose();
    }
}
