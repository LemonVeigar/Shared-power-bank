import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 使用事件调度线程来确保线程安全
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // 创建并显示登录界面
                new LoginFrame().setVisible(true);
                System.out.println("登录界面已显示");
            }
        });
    }
}
