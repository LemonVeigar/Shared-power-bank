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
     */
    public AddPowerbankFrame() {
        // 设置窗口标题
        setTitle("添加充电宝");
        // 设置窗口大小
        setSize(400, 400);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
        setResizable(false);

        // 创建主面板并设置布局
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 定义标签和输入框的位置和填充
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 位置
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("位置:"), gbc);

        gbc.gridx = 1;
        locationField = new JTextField(20);
        panel.add(locationField, gbc);

        // 纬度
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("纬度:"), gbc);

        gbc.gridx = 1;
        latitudeField = new JTextField(20);
        panel.add(latitudeField, gbc);

        // 经度
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("经度:"), gbc);

        gbc.gridx = 1;
        longitudeField = new JTextField(20);
        panel.add(longitudeField, gbc);

        // 电量
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("电量 (%):"), gbc);

        gbc.gridx = 1;
        batteryLevelField = new JTextField(20);
        panel.add(batteryLevelField, gbc);

        // 状态
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("状态:"), gbc);

        gbc.gridx = 1;
        String[] statuses = {"available", "rented", "maintenance"};
        statusComboBox = new JComboBox<>(statuses);
        panel.add(statusComboBox, gbc);

        // 价格
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("价格 (元/小时):"), gbc);

        gbc.gridx = 1;
        pricePerHourField = new JTextField(20);
        panel.add(pricePerHourField, gbc);

        // 按钮面板
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("添加");
        cancelButton = new JButton("取消");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        // 将面板添加到窗口
        add(panel);

        // 添加按钮事件监听器
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPowerbank();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭当前窗口
                dispose();
            }
        });
    }

    /**
     * 处理添加充电宝的逻辑。
     */
    private void addPowerbank() {
        String location = locationField.getText().trim();
        String latitudeStr = latitudeField.getText().trim();
        String longitudeStr = longitudeField.getText().trim();
        String batteryLevelStr = batteryLevelField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();
        String pricePerHourStr = pricePerHourField.getText().trim();

        // 输入验证
        if (location.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()
                || batteryLevelStr.isEmpty() || pricePerHourStr.isEmpty()) {
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

            if (batteryLevel < 0 || batteryLevel > 100) {
                throw new NumberFormatException("电量必须在0到100之间");
            }

            if (pricePerHour.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException("价格必须为正数");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的数值：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 插入数据库
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = "INSERT INTO powerbanks (location, latitude, longitude, battery_level, status, price_per_hour) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, location);
            pstmt.setBigDecimal(2, new BigDecimal(latitude));
            pstmt.setBigDecimal(3, new BigDecimal(longitude));
            pstmt.setInt(4, batteryLevel);
            pstmt.setString(5, status);
            pstmt.setBigDecimal(6, pricePerHour);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "充电宝添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                // 清空输入字段
                locationField.setText("");
                latitudeField.setText("");
                longitudeField.setText("");
                batteryLevelField.setText("");
                pricePerHourField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "充电宝添加失败，请重试。", "错误", JOptionPane.ERROR_MESSAGE);
            }

            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库操作失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
