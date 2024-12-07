package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * AddPowerbankFrame类创建添加充电宝的界面，供管理员添加新的充电宝。
 */
public class AddPowerbankFrame extends JFrame {
    private String username; // 当前登录的管理员用户名
    private JTextField locationField;
    private JTextField latitudeField;
    private JTextField longitudeField;
    private JTextField batteryLevelField;
    private JComboBox<String> statusComboBox;
    private JTextField pricePerHourField;
    private JButton addButton;
    private JButton cancelButton;

    /**
     * 构造方法，初始化添加充电宝界面。
     *
     * @param username 当前登录的管理员用户名
     */
    public AddPowerbankFrame(String username) {
        this.username = username;

        // 设置窗口标题
        setTitle("添加充电宝 - 管理员: " + username);
        // 设置窗口大小
        setSize(400, 400);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
        setResizable(false);

        // 创建主面板并设置布局为GridLayout
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加标签和输入框
        panel.add(new JLabel("位置："));
        locationField = new JTextField();
        panel.add(locationField);

        panel.add(new JLabel("纬度："));
        latitudeField = new JTextField();
        panel.add(latitudeField);

        panel.add(new JLabel("经度："));
        longitudeField = new JTextField();
        panel.add(longitudeField);

        panel.add(new JLabel("剩余电量 (%)："));
        batteryLevelField = new JTextField();
        panel.add(batteryLevelField);

        panel.add(new JLabel("状态："));
        statusComboBox = new JComboBox<>(new String[]{"available", "unavailable", "maintenance"});
        panel.add(statusComboBox);

        panel.add(new JLabel("租赁价格 (元/小时)："));
        pricePerHourField = new JTextField();
        panel.add(pricePerHourField);

        // 添加按钮
        addButton = new JButton("添加");
        cancelButton = new JButton("取消");
        panel.add(addButton);
        panel.add(cancelButton);

        // 将主面板添加到窗口
        add(panel);

        // 为添加按钮添加点击事件监听器
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddPowerbank();
            }
        });

        // 为取消按钮添加点击事件监听器
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭窗口
            }
        });
    }

    /**
     * 处理添加充电宝的逻辑。
     */
    private void handleAddPowerbank() {
        // 获取输入数据
        String location = locationField.getText().trim();
        String latitudeStr = latitudeField.getText().trim();
        String longitudeStr = longitudeField.getText().trim();
        String batteryLevelStr = batteryLevelField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();
        String pricePerHourStr = pricePerHourField.getText().trim();

        // 输入验证
        if (location.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty() ||
                batteryLevelStr.isEmpty() || pricePerHourStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有字段", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double latitude, longitude;
        int batteryLevel;
        BigDecimal pricePerHour;
        try {
            latitude = Double.parseDouble(latitudeStr);
            longitude = Double.parseDouble(longitudeStr);
            batteryLevel = Integer.parseInt(batteryLevelStr);
            pricePerHour = new BigDecimal(pricePerHourStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的数值", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 连接数据库并执行插入操作
        String insertSql = "INSERT INTO powerbanks (location, latitude, longitude, battery_level, status, price_per_hour) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            pstmt.setString(1, location);
            pstmt.setDouble(2, latitude);
            pstmt.setDouble(3, longitude);
            pstmt.setInt(4, batteryLevel);
            pstmt.setString(5, status);
            pstmt.setBigDecimal(6, pricePerHour);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "充电宝添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // 关闭窗口
            } else {
                JOptionPane.showMessageDialog(this, "充电宝添加失败", "错误", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "添加充电宝时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
