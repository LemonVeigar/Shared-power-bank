package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;
import com.spbsysteam.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * AddEditUserDialog类创建添加或编辑用户的对话框。
 */
public class AddEditUserDialog extends JDialog {
    private String username; // 当前登录的用户名
    private String role;     // 当前用户的角色

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton saveButton;
    private JButton cancelButton;

    private User user; // 如果为编辑，存储要编辑的用户

    /**
     * 构造方法，初始化添加或编辑用户的对话框。
     *
     * @param parent 父窗口
     * @param title  对话框标题
     * @param user   要编辑的用户，如果为添加则为null
     * @param role   当前用户的角色（如 "admin" 或 "user"）
     */
    public AddEditUserDialog(JFrame parent, String title, User user, String role) {
        super(parent, title, true);
        this.user = user;
        this.role = role;

        // 权限验证
        if (!"admin".equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(this, "您没有访问此页面的权限。", "权限不足", JOptionPane.ERROR_MESSAGE);
            dispose(); // 关闭窗口
            return;
        }

        // 初始化UI组件
        initUI();
    }

    private void initUI() {
        // 设置窗口属性
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // 创建主面板并设置布局为GridLayout
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加标签和输入框
        panel.add(new JLabel("用户名："));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("密码："));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("角色："));
        roleComboBox = new JComboBox<>(new String[]{"user", "admin"});
        panel.add(roleComboBox);

        // 添加保存和取消按钮
        saveButton = new JButton("保存");
        cancelButton = new JButton("取消");
        panel.add(saveButton);
        panel.add(cancelButton);

        // 如果是编辑，填充已有数据并禁用用户名编辑
        if (user != null) {
            usernameField.setText(user.getUsername());
            usernameField.setEnabled(false); // 不允许修改用户名
            // 密码字段留空，允许管理员设置新密码
            roleComboBox.setSelectedItem(user.getRole());
        }

        // 将主面板添加到对话框
        add(panel);

        // 添加按钮事件监听器
        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> dispose());
    }

    private void handleSave() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String selectedRole = (String) roleComboBox.getSelectedItem();

        // 输入验证
        if (username.isEmpty() || (user == null && password.isEmpty())) {
            JOptionPane.showMessageDialog(this, "请填写所有必填字段", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 连接数据库并执行插入或更新操作
        if (user == null) {
            // 添加操作
            String insertSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                pstmt.setString(1, username);
                pstmt.setString(2, password); // 注意：密码应加密存储
                pstmt.setString(3, selectedRole);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "用户添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 关闭窗口
                } else {
                    JOptionPane.showMessageDialog(this, "用户添加失败", "错误", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "添加用户时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // 编辑操作
            String updateSql;
            if (!password.isEmpty()) {
                // 如果管理员输入了新密码，则更新密码
                updateSql = "UPDATE users SET password = ?, role = ? WHERE id = ?";
            } else {
                // 否则，仅更新角色
                updateSql = "UPDATE users SET role = ? WHERE id = ?";
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.isEmpty()) {
                    pstmt.setString(1, password); // 注意：密码应加密存储
                    pstmt.setString(2, selectedRole);
                    pstmt.setInt(3, user.getId());
                } else {
                    pstmt.setString(1, selectedRole);
                    pstmt.setInt(2, user.getId());
                }

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "用户更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 关闭窗口
                } else {
                    JOptionPane.showMessageDialog(this, "用户更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "更新用户时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
