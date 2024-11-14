package com.sharedpowerbank;

import java.util.HashMap;
import java.util.Scanner;

public class PowerBankRentalSystem {

    // 使用HashMap来模拟数据库，键为用户名，值为密码
    private HashMap<String, String> users = new HashMap<>();


    // 创建Scanner对象，用于获取用户在控制台的输入
    private Scanner scanner = new Scanner(System.in);


    //主方法，程序的入口
    public static void main(String[] args) {
        // 创建系统对象并启动系统
        PowerBankRentalSystem system = new PowerBankRentalSystem();
        system.start();
    }

    // 系统启动方法，显示欢迎信息并提供操作选项
    public void start() {
        System.out.println("欢迎进入移动电源租赁系统"); //打印欢迎信息
        while (true) {  // 无限循环，直到用户选择退出
            System.out.println("请选择操作：1.注册  2.登录  3.退出");  // 打印操作选项
            String choice = scanner.nextLine();  //获取用户的选择
            switch (choice) {  //根据用户输入选择操作
                case "1":
                    register();  //用户选择“1”时调用register()方法进行注册
                    break;
                case "2":
                    login();  //用户选择“2”时调用login()方法进行登录
                    break;
                case "3":
                    System.out.println("退出系统。再见！"); //打印退出信息
                    return; //退出程序
                default:
                    System.out.println("无效选项，请重新选择");  //输入无效时提示用户重新选择
            }
        }
    }
    // 注册方法，允许新用户注册账号
    public void register() {
        System.out.println("请输入要注册的用户名：");  // 提示用户输入用户名
        String username = scanner.nextLine();  // 获取用户输入的用户名
        if (users.containsKey(username)) {  // 检查用户名是否已存在
            System.out.println("该用户名已存在，请选择其他用户名。");  // 如果用户名已存在，提示用户
            return;  // 返回，不继续执行后续注册操作
        }
        System.out.println("请输入密码：");  // 提示用户输入密码
        String password = scanner.nextLine();  // 获取用户输入的密码
        users.put(username, password);  // 将用户名和密码存储到users HashMap中
        System.out.println("注册成功！请登录。");  // 提示用户注册成功
    }

    // 登录方法，允许已注册的用户登录
    public void login() {
        System.out.println("请输入用户名：");  // 提示用户输入用户名
        String username = scanner.nextLine();  // 获取用户输入的用户名
        System.out.println("请输入密码：");  // 提示用户输入密码
        String password = scanner.nextLine();  // 获取用户输入的密码

        // 验证用户名和密码是否匹配
        if (users.containsKey(username) && users.get(username).equals(password)) {
            System.out.println("登录成功！欢迎使用系统。");  // 如果用户名和密码正确，提示登录成功
            enterSystem();  // 调用enterSystem()方法进入系统
        } else {
            System.out.println("用户名或密码错误，请重试。");  // 如果用户名或密码错误，提示用户
        }
    }

    // 模拟进入系统后的操作，后续功能可以扩展在这里
    public void enterSystem() {
        System.out.println("您已进入系统，后续功能待实现。");  // 提示用户已进入系统
        // 这里可以添加租赁系统的其他功能
    }
}
