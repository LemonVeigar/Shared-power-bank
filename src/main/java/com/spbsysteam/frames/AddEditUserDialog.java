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
     */
    public AddEditUserDialog(JFrame parent, String title, User user) {
        super(parent, title, true);
        this.user = user;

        // 设置对话框大小
        setSize(400, 300);
        // 设置对话框居中
        setLocationRelativeTo(parent);
        // 禁止调整大小
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

        // 将主面板添加到对话框
        add(panel);

        // 如果是编辑，填充已有数据并禁用用户名编辑
        if (user != null) {
            usernameField.setText(user.getUsername());
            usernameField.setEnabled(false); // 不允许修改用户名
            // 密码不显示，管理员可选择输入新密码
            roleComboBox.setSelectedItem(user.getRole());
        }

        // 为保存按钮添加点击事件监听器
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave();
            }
        });

        // 为取消按钮添加点击事件监听器
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭对话框
            }
        });
    }

    /**
     * 处理保存用户信息的逻辑。
     */
    private void handleSave() {
        // 获取输入数据
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

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
                pstmt.setString(2, password); // 密码应加密存储
                pstmt.setString(3, role);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "用户添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 关闭对话框
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
                    pstmt.setString(1, password); // 密码应加密存储
                    pstmt.setString(2, role);
                    pstmt.setInt(3, user.getId());
                } else {
                    pstmt.setString(1, role);
                    pstmt.setInt(2, user.getId());
                }

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "用户更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 关闭对话框
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
