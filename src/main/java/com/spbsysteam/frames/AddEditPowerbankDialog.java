package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;
import com.spbsysteam.models.Powerbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;

public class AddEditPowerbankDialog extends JDialog {
    private String username; // 当前登录的用户名
    private String role;     // 当前用户的角色

    private JTextField locationField;
    private JTextField latitudeField;
    private JTextField longitudeField;
    private JTextField batteryLevelField;
    private JComboBox<String> statusComboBox;
    private JTextField pricePerHourField;
    private JButton saveButton;
    private JButton cancelButton;

    private Powerbank powerbank; // 如果为编辑，存储要编辑的充电宝

    /**
     * 构造方法，初始化添加或编辑充电宝的对话框。
     *
     * @param parent    父窗口
     * @param title     对话框标题
     * @param powerbank 要编辑的充电宝，如果为添加则为null
     * @param role      当前用户的角色
     */
    public AddEditPowerbankDialog(JFrame parent, String title, Powerbank powerbank, String role) {
        super(parent, title, true);
        this.powerbank = powerbank;
        this.role = role;

        // 只有管理员才能执行此操作
        if (!"admin".equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(this, "您没有访问此页面的权限。", "权限不足", JOptionPane.ERROR_MESSAGE);
            dispose(); // 关闭窗口
            return;
        }

        // 设置窗口属性
        setSize(400, 400);
        setLocationRelativeTo(parent);
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
        saveButton = new JButton("保存");
        cancelButton = new JButton("取消");
        panel.add(saveButton);
        panel.add(cancelButton);

        // 将主面板添加到对话框
        add(panel);

        // 如果是编辑，填充已有数据
        if (powerbank != null) {
            locationField.setText(powerbank.getLocation());
            latitudeField.setText(String.valueOf(powerbank.getLatitude()));
            longitudeField.setText(String.valueOf(powerbank.getLongitude()));
            batteryLevelField.setText(String.valueOf(powerbank.getBatteryLevel()));
            statusComboBox.setSelectedItem(powerbank.getStatus());
            pricePerHourField.setText(powerbank.getPricePerHour().toString());
        }

        // 为保存按钮添加点击事件监听器
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave();
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
     * 处理保存充电宝信息的逻辑。
     */
    private void handleSave() {
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

        // 验证当前用户是否为管理员（冗余验证，提升安全性）
        if (!"admin".equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(this, "您没有权限执行此操作。", "权限不足", JOptionPane.ERROR_MESSAGE);
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

        // 连接数据库并执行插入或更新操作
        if (powerbank == null) {
            // 添加操作
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
        } else {
            // 编辑操作
            String updateSql = "UPDATE powerbanks SET location = ?, latitude = ?, longitude = ?, battery_level = ?, status = ?, price_per_hour = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

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
                pstmt.setInt(7, powerbank.getId());

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "充电宝更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 关闭窗口
                } else {
                    JOptionPane.showMessageDialog(this, "充电宝更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "更新充电宝时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
