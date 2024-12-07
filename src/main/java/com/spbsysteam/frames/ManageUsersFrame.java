package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;
import com.spbsysteam.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/**
 * ManageUsersFrame类创建管理用户的界面，供管理员进行用户的增删改查操作。
 */
public class ManageUsersFrame extends JFrame {
    private String username; // 当前登录的管理员用户名
    private JTable userTable; // 用户信息表格
    private JButton addButton; // 添加用户按钮
    private JButton editButton; // 编辑用户按钮
    private JButton deleteButton; // 删除用户按钮
    private JButton refreshButton; // 刷新按钮
    private JButton backButton; // 返回按钮

    /**
     * 构造方法，初始化管理用户界面。
     *
     * @param username 当前登录的管理员用户名
     */
    public ManageUsersFrame(String username) {
        this.username = username; // 保存用户名

        // 设置窗口标题
        setTitle("管理用户 - 管理员: " + username);
        // 设置窗口大小
        setSize(900, 600);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
        setResizable(false);

        // 创建主面板并设置布局为BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // 创建并添加顶部标签
        JLabel titleLabel = new JLabel("管理用户", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // 创建表格模型并定义列名
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID"); // 用户ID
        tableModel.addColumn("用户名"); // 用户名
        tableModel.addColumn("角色"); // 角色
        // 可以根据需要添加更多用户信息列

        // 创建表格并设置模型
        userTable = new JTable(tableModel) {
            // 禁止编辑表格单元格
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选模式

        // 设置表格列宽
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150); // 用户名
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 角色

        // 将表格放入滚动面板
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板并设置布局为FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 创建并添加添加用户按钮
        addButton = new JButton("添加用户");
        buttonPanel.add(addButton);

        // 创建并添加编辑用户按钮
        editButton = new JButton("编辑用户");
        buttonPanel.add(editButton);

        // 创建并添加删除用户按钮
        deleteButton = new JButton("删除用户");
        buttonPanel.add(deleteButton);

        // 创建并添加刷新按钮
        refreshButton = new JButton("刷新");
        buttonPanel.add(refreshButton);

        // 创建并添加返回按钮
        backButton = new JButton("返回管理员界面");
        buttonPanel.add(backButton);

        // 将按钮面板添加到主面板
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗口
        add(panel);

        // 加载用户数据到表格
        loadUserData(tableModel);

        // 为添加用户按钮添加点击事件监听器
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 弹出添加用户对话框
                AddEditUserDialog addDialog = new AddEditUserDialog(ManageUsersFrame.this, "添加用户", null);
                addDialog.setVisible(true);
                // 刷新表格数据
                loadUserData(tableModel);
            }
        });

        // 为编辑用户按钮添加点击事件监听器
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取选中的用户信息
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ManageUsersFrame.this, "请选择要编辑的用户", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 获取用户信息
                int userId = (int) userTable.getValueAt(selectedRow, 0);
                String username = (String) userTable.getValueAt(selectedRow, 1);
                String role = (String) userTable.getValueAt(selectedRow, 2);

                // 创建用户对象
                User user = new User(userId, username, role);

                // 弹出编辑用户对话框
                AddEditUserDialog editDialog = new AddEditUserDialog(ManageUsersFrame.this, "编辑用户", user);
                editDialog.setVisible(true);
                // 刷新表格数据
                loadUserData(tableModel);
            }
        });

        // 为删除用户按钮添加点击事件监听器
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取选中的用户ID和用户名
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ManageUsersFrame.this, "请选择要删除的用户", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int userId = (int) userTable.getValueAt(selectedRow, 0);
                String username = (String) userTable.getValueAt(selectedRow, 1);

                // 确认删除
                int confirm = JOptionPane.showConfirmDialog(ManageUsersFrame.this, "确定要删除用户：" + username + " 吗？", "确认删除", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                // 删除用户
                String deleteSql = "DELETE FROM users WHERE id = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {

                    if (conn == null) {
                        JOptionPane.showMessageDialog(ManageUsersFrame.this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    pstmt.setInt(1, userId);
                    int rowsDeleted = pstmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(ManageUsersFrame.this, "用户删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                        loadUserData(tableModel);
                    } else {
                        JOptionPane.showMessageDialog(ManageUsersFrame.this, "用户删除失败", "错误", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ManageUsersFrame.this, "删除用户时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 为刷新按钮添加点击事件监听器
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 重新加载用户数据
                loadUserData(tableModel);
            }
        });

        // 为返回按钮添加点击事件监听器
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 返回管理员界面
                AdminFrame adminFrame = new AdminFrame(username); // 传递用户名
                adminFrame.setVisible(true);
                // 关闭当前界面
                dispose();
            }
        });
    }

    /**
     * 从数据库加载用户数据并填充到表格模型中。
     *
     * @param tableModel 表格模型
     */
    private void loadUserData(DefaultTableModel tableModel) {
        String sql = "SELECT * FROM users";

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
                row.add(rs.getString("username")); // 添加用户名
                row.add(rs.getString("role")); // 添加角色
                // 可以根据需要添加更多用户信息
                tableModel.addRow(row); // 将行添加到表格模型
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载用户数据时发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
