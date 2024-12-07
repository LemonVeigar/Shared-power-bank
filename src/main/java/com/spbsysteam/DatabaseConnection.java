package com.spbsysteam;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection类用于管理与MySQL数据库的连接。
 */
public class DatabaseConnection {
    // 数据库URL，包含数据库名shared_power_bank
    private static final String URL = "jdbc:mysql://localhost:3306/shared_power_bank?useSSL=false&serverTimezone=UTC";
    // 数据库用户名
    private static final String USER = "root";
    // 数据库密码
    private static final String PASSWORD = "111111"; // 实际密码

    /**
     * 获取一个新的数据库连接。
     *
     * @return 一个新的Connection对象
     */
    public static Connection getConnection() {
        try {
            // 加载MySQL JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 返回新的数据库连接
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            // JDBC驱动未找到
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "JDBC驱动未找到", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (SQLException e) {
            // 连接数据库失败
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法连接到数据库", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
