package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;
import com.spbsysteam.models.Powerbank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.Vector;

/**
 * ManagePowerbanksFrame类创建管理充电宝的界面，供管理员进行充电宝的增删改查操作。
 */
public class ManagePowerbanksFrame extends JFrame {
    private String username; // 当前登录的用户名
    private String role;     // 当前用户的角色

    private JTable powerbankTable; // 充电宝信息表格
    private JButton addButton;     // 添加充电宝按钮
    private JButton editButton;    // 编辑充电宝按钮
    private JButton deleteButton;  // 删除充电宝按钮
    private JButton refreshButton; // 刷新按钮
    private JButton backButton;    // 返回按钮

    /**
     * 构造方法，初始化管理充电宝界面。
     *
     * @param username 当前登录的用户名
     * @param role     当前用户的角色（如 "admin" 或 "user"）
     */
    public ManagePowerbanksFrame(String username, String role) {
        this.username = username;
        this.role = role;

        // 权限验证：只有管理员才能访问此界面
        if (!"admin".equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(this, "您没有访问此页面的权限。", "权限不足", JOptionPane.ERROR_MESSAGE);
            dispose(); // 关闭窗口
            return;
        }

        // 设置窗口属性
        setTitle("管理充电宝 - 用户: " + username);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 创建主面板并设置布局为BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // 创建并添加顶部标签
        JLabel titleLabel = new JLabel("管理充电宝", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // 创建表格模型并定义列名
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID"); // 充电宝ID
        tableModel.addColumn("位置"); // 位置
        tableModel.addColumn("纬度"); // 纬度
        tableModel.addColumn("经度"); // 经度
        tableModel.addColumn("剩余电量 (%)"); // 剩余电量
        tableModel.addColumn("状态"); // 状态
        tableModel.addColumn("租赁价格 (元/小时)"); // 租赁价格

        // 创建表格并设置模型
        powerbankTable = new JTable(tableModel) {
            // 禁止编辑表格单元格
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        powerbankTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选模式

        // 设置表格列宽（可选）
        powerbankTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        powerbankTable.getColumnModel().getColumn(1).setPreferredWidth(150); // 位置
        powerbankTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 纬度
        powerbankTable.getColumnModel().getColumn(3).setPreferredWidth(100); // 经度
        powerbankTable.getColumnModel().getColumn(4).setPreferredWidth(100); // 剩余电量
        powerbankTable.getColumnModel().getColumn(5).setPreferredWidth(100); // 状态
        powerbankTable.getColumnModel().getColumn(6).setPreferredWidth(150); // 租赁价格

        // 将表格放入滚动面板
        JScrollPane scrollPane = new JScrollPane(powerbankTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板并设置布局为FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 初始化按钮
        addButton = new JButton("添加充电宝");
        editButton = new JButton("编辑充电宝");
        deleteButton = new JButton("删除充电宝");
        refreshButton = new JButton("刷新");
        backButton = new JButton("返回管理员界面");

        // 添加按钮到按钮面板
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        // 将按钮面板添加到主面板
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗口
        add(panel);

        // 加载充电宝数据到表格
        loadPowerbankData(tableModel);

        // 添加充电宝按钮的事件监听器
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("添加充电宝按钮被点击");

                    if ("admin".equalsIgnoreCase(role)) {
                        System.out.println("当前用户为管理员，打开添加充电宝对话框");
                        AddEditPowerbankDialog addDialog = new AddEditPowerbankDialog(ManagePowerbanksFrame.this, "添加充电宝", null, role);
                        addDialog.setVisible(true);
                        // 刷新表格数据
                        loadPowerbankData(tableModel);
                    } else {
                        System.out.println("当前用户为普通用户，显示权限不足提示");
                        JOptionPane.showMessageDialog(ManagePowerbanksFrame.this,
                                "权限不足，无法使用该功能。",
                                "权限提示",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ManagePowerbanksFrame.this,
                            "发生错误：" + ex.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 编辑充电宝按钮的事件监听器
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("编辑充电宝按钮被点击");

                    if ("admin".equalsIgnoreCase(role)) {
                        // 获取选中的充电宝
                        int selectedRow = powerbankTable.getSelectedRow();
                        if (selectedRow == -1) {
                            JOptionPane.showMessageDialog(ManagePowerbanksFrame.this, "请选择要编辑的充电宝", "提示", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // 从表格中获取充电宝信息
                        int powerbankId = (int) powerbankTable.getValueAt(selectedRow, 0);
                        String location = (String) powerbankTable.getValueAt(selectedRow, 1);
                        double latitude = (double) powerbankTable.getValueAt(selectedRow, 2);
                        double longitude = (double) powerbankTable.getValueAt(selectedRow, 3);
                        int batteryLevel = (int) powerbankTable.getValueAt(selectedRow, 4);
                        String status = (String) powerbankTable.getValueAt(selectedRow, 5);
                        BigDecimal pricePerHour = (BigDecimal) powerbankTable.getValueAt(selectedRow, 6);

                        // 创建 Powerbank 对象（假设有对应的构造器）
                        Powerbank powerbank = new Powerbank(powerbankId, location, latitude, longitude, batteryLevel, status, pricePerHour);

                        // 打开编辑充电宝对话框
                        AddEditPowerbankDialog editDialog = new AddEditPowerbankDialog(ManagePowerbanksFrame.this, "编辑充电宝", powerbank, role);
                        editDialog.setVisible(true);
                        // 刷新表格数据
                        loadPowerbankData(tableModel);
                    } else {
                        System.out.println("当前用户为普通用户，显示权限不足提示");
                        JOptionPane.showMessageDialog(ManagePowerbanksFrame.this,
                                "权限不足，无法使用该功能。",
                                "权限提示",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ManagePowerbanksFrame.this,
                            "发生错误：" + ex.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 删除充电宝按钮的事件监听器
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("删除充电宝按钮被点击");

                    if ("admin".equalsIgnoreCase(role)) {
                        // 获取选中的充电宝
                        int selectedRow = powerbankTable.getSelectedRow();
                        if (selectedRow == -1) {
                            JOptionPane.showMessageDialog(ManagePowerbanksFrame.this, "请选择要删除的充电宝", "提示", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // 从表格中获取充电宝信息
                        int powerbankId = (int) powerbankTable.getValueAt(selectedRow, 0);
                        String location = (String) powerbankTable.getValueAt(selectedRow, 1);

                        // 确认删除
                        int confirm = JOptionPane.showConfirmDialog(ManagePowerbanksFrame.this,
                                "确定要删除充电宝：" + location + " 吗？",
                                "确认删除",
                                JOptionPane.YES_NO_OPTION);
                        if (confirm != JOptionPane.YES_OPTION) {
                            return;
                        }

                        // 删除充电宝
                        String deleteSql = "DELETE FROM powerbanks WHERE id = ?";
                        try (Connection conn = DatabaseConnection.getConnection();
                             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {

                            if (conn == null) {
                                JOptionPane.showMessageDialog(ManagePowerbanksFrame.this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            pstmt.setInt(1, powerbankId);
                            int rowsDeleted = pstmt.executeUpdate();
                            if (rowsDeleted > 0) {
                                JOptionPane.showMessageDialog(ManagePowerbanksFrame.this, "充电宝删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                                loadPowerbankData(tableModel);
                            } else {
                                JOptionPane.showMessageDialog(ManagePowerbanksFrame.this, "充电宝删除失败", "错误", JOptionPane.ERROR_MESSAGE);
                            }

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(ManagePowerbanksFrame.this, "删除充电宝时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        System.out.println("当前用户为普通用户，显示权限不足提示");
                        JOptionPane.showMessageDialog(ManagePowerbanksFrame.this,
                                "权限不足，无法使用该功能。",
                                "权限提示",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ManagePowerbanksFrame.this,
                            "发生错误：" + ex.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 刷新按钮的事件监听器
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("刷新按钮被点击");
                    loadPowerbankData(tableModel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ManagePowerbanksFrame.this,
                            "发生错误：" + ex.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 返回按钮的事件监听器
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("返回管理员界面按钮被点击");
                    AdminFrame adminFrame = new AdminFrame(username, role);
                    adminFrame.setVisible(true);
                    dispose(); // 关闭当前窗口
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ManagePowerbanksFrame.this,
                            "发生错误：" + ex.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * 从数据库加载充电宝数据并填充到表格模型中。
     *
     * @param tableModel 表格模型
     */
    private void loadPowerbankData(DefaultTableModel tableModel) {
        String sql = "SELECT * FROM powerbanks";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "数据库连接失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 清空现有表格数据
            tableModel.setRowCount(0);

            // 遍历结果集并添加到表格模型
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id")); // 添加ID
                row.add(rs.getString("location")); // 添加位置
                row.add(rs.getDouble("latitude")); // 添加纬度
                row.add(rs.getDouble("longitude")); // 添加经度
                row.add(rs.getInt("battery_level")); // 添加剩余电量

                // 获取状态并转换为中文显示（可选）
                String dbStatus = rs.getString("status");
                String displayStatus;
                switch (dbStatus.toLowerCase()) {
                    case "available":
                        displayStatus = "可用";
                        break;
                    case "unavailable":
                        displayStatus = "不可用";
                        break;
                    case "maintenance":
                        displayStatus = "维修中";
                        break;
                    default:
                        displayStatus = dbStatus; // 其他状态保持不变
                }

                row.add(displayStatus); // 添加状态
                row.add(rs.getBigDecimal("price_per_hour")); // 添加租赁价格
                tableModel.addRow(row); // 将行添加到表格模型
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载充电宝数据时发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
