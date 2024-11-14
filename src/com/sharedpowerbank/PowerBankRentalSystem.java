package com.sharedpowerbank;

import java.util.HashMap;
import java.util.Scanner;

public class PowerBankRentalSystem {

    // 使用HashMap来模拟数据库，键为用户名，值为密码
    private HashMap<String, String> user = new HashMap<>();


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
}
