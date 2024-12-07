package com.spbsysteam.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * AdminFrame类创建管理员管理界面，供管理员管理充电宝和用户信息。
 */
public class AdminFrame extends JFrame {
    private String username; // 当前登录的管理员用户名
    private JButton managePowerbanksButton; // 管理充电宝按钮
    private JButton manageUsersButton; // 管理用户按钮
    private JButton backButton; // 返回按钮

    /**
     * 构造方法，初始化管理员管理界面。
     *
     * @param username 当前登录的管理员用户名
     */
    public AdminFrame(String username) {
        this.username = username; // 保存用户名

        // 设置窗口标题
        setTitle("共享充电宝租赁系统 - 管理员管理");
        // 设置窗口大小
        setSize(600, 400);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
        setResizable(false);

        // 创建主面板并设置布局为BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // 创建并添加顶部标签
        JLabel titleLabel = new JLabel("管理员管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24)); // 使用支持中文的字体
        panel.add(titleLabel, BorderLayout.NORTH);

        // 创建中间按钮面板，使用GridLayout布局
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 200, 50, 200)); // 上左下右边距

        // 初始化按钮
        managePowerbanksButton = new JButton("管理充电宝");
        manageUsersButton = new JButton("管理用户");

        // 添加按钮到按钮面板
        buttonPanel.add(managePowerbanksButton);
        buttonPanel.add(manageUsersButton);

        // 将按钮面板添加到主面板
        panel.add(buttonPanel, BorderLayout.CENTER);

        // 创建底部按钮面板
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        backButton = new JButton("返回主界面");
        bottomPanel.add(backButton);

        // 添加底部按钮面板到主面板
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗口
        add(panel);

        // 添加事件监听器
        managePowerbanksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("管理充电宝按钮被点击");
                ManagePowerbanksFrame managePowerbanksFrame = new ManagePowerbanksFrame(username);
                managePowerbanksFrame.setVisible(true);
                dispose(); // 关闭当前界面
            }
        });

        manageUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("管理用户按钮被点击");
                ManageUsersFrame manageUsersFrame = new ManageUsersFrame(username);
                manageUsersFrame.setVisible(true);
                dispose(); // 关闭当前界面
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("返回主界面按钮被点击");
                MainFrame mainFrame = new MainFrame(username);
                mainFrame.setVisible(true);
                dispose(); // 关闭当前界面
            }
        });
    }
}
