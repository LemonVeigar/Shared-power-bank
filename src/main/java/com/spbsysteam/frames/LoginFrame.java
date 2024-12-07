package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;
import com.spbsysteam.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * LoginFrame类创建用户登录界面。
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    /**
     * 构造方法，初始化登录界面。
     */
    public LoginFrame() {
        // 设置窗口标题
        setTitle("用户登录");
        // 设置窗口大小
        setSize(400, 200);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
        setResizable(false);

        // 创建主面板并设置布局为GridLayout
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加用户名和密码标签及输入框
        panel.add(new JLabel("用户名："));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("密码："));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        // 添加登录和注册按钮
        loginButton = new JButton("登录");
        registerButton = new JButton("注册");
        panel.add(loginButton);
        panel.add(registerButton);

        // 将主面板添加到窗口
        add(panel);

        // 为登录按钮添加点击事件监听器
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // 为注册按钮添加点击事件监听器
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 打开注册界面
                RegisterFrame registerFrame = new RegisterFrame();
                registerFrame.setVisible(true);
                // 关闭登录界面
                dispose();
            }
        });
    }

    /**
     * 处理用户登录逻辑。
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 连接数据库验证用户
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?"; // 注意：密码应加密存储

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            pstmt.setString(1, username);
            pstmt.setString(2, password); // 密码应加密存储

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    int userId = rs.getInt("id");
                    User user = new User(userId, username, role);

                    JOptionPane.showMessageDialog(this, "登录成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    // 根据角色打开相应的界面
                    if ("admin".equalsIgnoreCase(role)) {
                        AdminFrame adminFrame = new AdminFrame(username);
                        adminFrame.setVisible(true);
                    } else {
                        MainFrame mainFrame = new MainFrame(username);
                        mainFrame.setVisible(true);
                    }
                    // 关闭登录界面
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "登录过程中发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
