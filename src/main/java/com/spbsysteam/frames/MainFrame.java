package com.spbsysteam.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MainFrame类创建主界面，供用户进行后续操作。
 */
public class MainFrame extends JFrame {
    private String username; // 当前登录的用户名
    private JButton viewAvailablePowerbanksButton; // 查看可用充电宝按钮
    private JButton viewOrdersButton; // 查看租借记录按钮
    private JButton profileButton; // 查看/编辑个人信息按钮
    private JButton addPowerbankButton; // 添加充电宝按钮
    private JButton logoutButton; // 退出登录按钮
    private String role;     // 当前用户的角色

    /**
     * 构造方法，初始化主界面。
     *
     * @param username 当前登录的用户名
     * @param role     当前用户的角色（如 "admin" 或 "user"）
     */
    public MainFrame(String username, String role) {
        this.username = username;
        this.role = role;

        // 调试输出，确认传递的参数
        System.out.println("MainFrame 构造器调用，username: " + username + ", role: " + role);

        // 设置窗口标题
        setTitle("共享充电宝租赁系统 - 主界面");
        // 设置窗口大小
        setSize(600, 500);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
        setResizable(false);

        // 创建主面板并设置布局
        JPanel panel = new JPanel(new BorderLayout());

        // 创建欢迎标签
        JLabel welcomeLabel = new JLabel("欢迎, " + username + "！", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 20)); // 更换为支持中文的字体
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 10, 10)); // 6行1列，间隔10px

        // 实例化按钮
        viewAvailablePowerbanksButton = new JButton("查看可用充电宝");
        viewOrdersButton = new JButton("查看租借记录");
        profileButton = new JButton("查看/编辑个人信息");
        addPowerbankButton = new JButton("添加充电宝");
        JButton viewMyOrdersButton = new JButton("查看我的订单"); // 使用不同的变量名
        logoutButton = new JButton("退出登录");

        // 添加按钮到按钮面板
        buttonPanel.add(viewAvailablePowerbanksButton);
        buttonPanel.add(viewOrdersButton);
        buttonPanel.add(profileButton);
        buttonPanel.add(addPowerbankButton);
        buttonPanel.add(viewMyOrdersButton); // 添加“查看我的订单”按钮
        buttonPanel.add(logoutButton);

        // 根据角色控制按钮的可见性或可用性
        if (!"admin".equalsIgnoreCase(role)) {
            addPowerbankButton.setVisible(false); // 普通用户不显示“添加充电宝”按钮
            System.out.println("角色非admin，隐藏添加充电宝按钮");
        } else {
            System.out.println("角色为admin，显示添加充电宝按钮");
        }

        // 将按钮面板添加到主面板中心
        panel.add(buttonPanel, BorderLayout.CENTER);

        // 将主面板添加到窗口
        add(panel);

        // 为查看可用充电宝按钮添加点击事件监听器
        viewAvailablePowerbanksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("点击了查看可用充电宝按钮");
                // 打开查看可用充电宝界面
                openViewAvailablePowerbanksFrame();
            }
        });

        // 为查看租借记录按钮添加点击事件监听器
        viewOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("点击了查看租借记录按钮");
                // 打开租借记录界面
                openOrderHistoryFrame();
            }
        });

        // 为查看/编辑个人信息按钮添加点击事件监听器
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("点击了查看/编辑个人信息按钮");
                // 打开用户信息管理界面
                openUserProfileFrame();
            }
        });

        // 为添加充电宝按钮添加点击事件监听器
        addPowerbankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 仅调用 openAddPowerbankFrame()，避免重复创建对话框
                System.out.println("点击了添加充电宝按钮");
                openAddPowerbankFrame();
            }
        });

        // 为“查看我的订单”按钮添加点击事件监听器
        viewMyOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("点击了查看我的订单按钮");
                // 打开查看用户订单界面，传递 username 和 role
                ViewUserOrdersFrame ordersFrame = new ViewUserOrdersFrame(username, role);
                ordersFrame.setVisible(true);
                // 关闭主界面
                dispose();
            }
        });

        // 为退出登录按钮添加点击事件监听器
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("点击了退出登录按钮");
                // 返回登录界面
                logout();
            }
        });

        System.out.println("com.spbsysteam.frames.MainFrame 初始化完成");
    }

    /**
     * 打开查看可用充电宝界面的方法。
     */
    private void openViewAvailablePowerbanksFrame() {
        System.out.println("打开 com.spbsysteam.frames.ViewAvailablePowerbanksFrame");
        // 创建并显示查看可用充电宝界面
        ViewAvailablePowerbanksFrame viewAvailableFrame = new ViewAvailablePowerbanksFrame(username, role);
        viewAvailableFrame.setVisible(true);
        System.out.println("com.spbsysteam.frames.ViewAvailablePowerbanksFrame 已打开");
        // 关闭主界面
        this.dispose();
        System.out.println("com.spbsysteam.frames.MainFrame 已关闭");
    }

    /**
     * 打开租借记录界面的方法。
     */
    private void openOrderHistoryFrame() {
        System.out.println("打开 com.spbsysteam.frames.OrderHistoryFrame");
        // 创建并显示租借记录界面
        OrderHistoryFrame orderHistoryFrame = new OrderHistoryFrame(username, role);
        orderHistoryFrame.setVisible(true);
        System.out.println("com.spbsysteam.frames.OrderHistoryFrame 已打开");
        // 关闭主界面
        this.dispose();
        System.out.println("com.spbsysteam.frames.MainFrame 已关闭");
    }

    /**
     * 打开用户信息管理界面的方法。
     */
    private void openUserProfileFrame() {
        System.out.println("打开 com.spbsysteam.frames.UserProfileFrame");
        // 创建并显示用户信息管理界面
        UserProfileFrame userProfileFrame = new UserProfileFrame(username, role);
        userProfileFrame.setVisible(true);
        System.out.println("com.spbsysteam.frames.UserProfileFrame 已打开");
        // 关闭主界面
        this.dispose();
        System.out.println("com.spbsysteam.frames.MainFrame 已关闭");
    }

    /**
     * 打开添加充电宝界面的方法。
     */
    private void openAddPowerbankFrame() {
        System.out.println("打开 com.spbsysteam.frames.AddPowerbankFrame");
        // 创建并显示添加充电宝界面
        AddPowerbankFrame addPowerbankFrame = new AddPowerbankFrame(username, role);
        addPowerbankFrame.setVisible(true);
        System.out.println("com.spbsysteam.frames.AddPowerbankFrame 已打开");
        // 不关闭主界面，允许多窗口操作
    }

    /**
     * 退出登录的方法，返回登录界面。
     */
    private void logout() {
        System.out.println("执行退出登录操作");
        // 创建并显示登录界面
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
        System.out.println("com.spbsysteam.frames.LoginFrame 已打开");
        // 关闭主界面
        this.dispose();
        System.out.println("com.spbsysteam.frames.MainFrame 已关闭");
    }
}
