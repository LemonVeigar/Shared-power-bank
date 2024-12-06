import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MainFrame类创建主界面，供用户进行后续操作。
 */
public class MainFrame extends JFrame {
    private String username; // 当前登录的用户名
    private JButton viewPowerbanksButton; // 查看充电宝按钮
    private JButton viewOrdersButton; // 查看租借记录按钮
    private JButton profileButton; // 查看/编辑个人信息按钮
    private JButton addPowerbankButton; // 添加充电宝按钮
    private JButton logoutButton; // 退出登录按钮

    /**
     * 构造方法，初始化主界面。
     *
     * @param username 当前登录的用户名
     */
    public MainFrame(String username) {
        this.username = username;
        System.out.println("MainFrame 构造方法被调用，用户名：" + username);

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
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10)); // 5行1列，间隔10px

        // 创建并添加查看充电宝按钮
        viewPowerbanksButton = new JButton("查看可用充电宝");
        buttonPanel.add(viewPowerbanksButton);

        // 创建并添加查看租借记录按钮
        viewOrdersButton = new JButton("查看租借记录");
        buttonPanel.add(viewOrdersButton);

        // 创建并添加查看/编辑个人信息按钮
        profileButton = new JButton("查看/编辑个人信息");
        buttonPanel.add(profileButton);

        // 创建并添加添加充电宝按钮
        addPowerbankButton = new JButton("添加充电宝");
        buttonPanel.add(addPowerbankButton);

        // 创建并添加退出登录按钮
        logoutButton = new JButton("退出登录");
        buttonPanel.add(logoutButton);

        // 添加按钮面板到主面板中心
        panel.add(buttonPanel, BorderLayout.CENTER);

        // 将主面板添加到窗口
        add(panel);

        // 为查看充电宝按钮添加点击事件监听器
        viewPowerbanksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("点击了查看充电宝按钮");
                // 打开充电宝列表界面
                openPowerbankListFrame();
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
                System.out.println("点击了添加充电宝按钮");
                // 打开添加充电宝界面
                openAddPowerbankFrame();
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

        System.out.println("MainFrame 初始化完成");
    }

    /**
     * 打开充电宝列表界面的方法。
     */
    private void openPowerbankListFrame() {
        System.out.println("打开 PowerbankListFrame");
        // 创建并显示充电宝列表界面
        PowerbankListFrame powerbankListFrame = new PowerbankListFrame(username);
        powerbankListFrame.setVisible(true);
        System.out.println("PowerbankListFrame 已打开");
        // 关闭主界面
        this.dispose();
        System.out.println("MainFrame 已关闭");
    }

    /**
     * 打开租借记录界面的方法。
     */
    private void openOrderHistoryFrame() {
        System.out.println("打开 OrderHistoryFrame");
        // 创建并显示租借记录界面
        OrderHistoryFrame orderHistoryFrame = new OrderHistoryFrame(username);
        orderHistoryFrame.setVisible(true);
        System.out.println("OrderHistoryFrame 已打开");
        // 关闭主界面
        this.dispose();
        System.out.println("MainFrame 已关闭");
    }

    /**
     * 打开用户信息管理界面的方法。
     */
    private void openUserProfileFrame() {
        System.out.println("打开 UserProfileFrame");
        // 创建并显示用户信息管理界面
        UserProfileFrame userProfileFrame = new UserProfileFrame(username);
        userProfileFrame.setVisible(true);
        System.out.println("UserProfileFrame 已打开");
        // 关闭主界面
        this.dispose();
        System.out.println("MainFrame 已关闭");
    }

    /**
     * 打开添加充电宝界面的方法。
     */
    private void openAddPowerbankFrame() {
        System.out.println("打开 AddPowerbankFrame");
        // 创建并显示添加充电宝界面
        AddPowerbankFrame addPowerbankFrame = new AddPowerbankFrame();
        addPowerbankFrame.setVisible(true);
        System.out.println("AddPowerbankFrame 已打开");
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
        System.out.println("LoginFrame 已打开");
        // 关闭主界面
        this.dispose();
        System.out.println("MainFrame 已关闭");
    }
}
