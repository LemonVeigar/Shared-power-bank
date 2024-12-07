package com.spbsysteam.frames;

import com.spbsysteam.DatabaseConnection;
import com.spbsysteam.models.Powerbank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Vector;

/**
 * ManagePowerbanksFrame类创建管理充电宝的界面，供管理员进行充电宝的增删改查操作。
 */
public class ManagePowerbanksFrame extends JFrame {
    private String username; // 当前登录的管理员用户名
    private JTable powerbankTable; // 充电宝信息表格
    private JButton addButton; // 添加充电宝按钮
    private JButton editButton; // 编辑充电宝按钮
    private JButton deleteButton; // 删除充电宝按钮
    private JButton refreshButton; // 刷新按钮
    private JButton backButton; // 返回按钮

    /**
     * 构造方法，初始化管理充电宝界面。
     *
     * @param username 当前登录的管理员用户名
     */
    public ManagePowerbanksFrame(String username) {
        this.username = username; // 保存用户名

        // 设置窗口标题
        setTitle("管理充电宝");
        // 设置窗口大小
        setSize(900, 600);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 禁止调整窗口大小
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
        tableModel.addColumn("位置"); // 充电宝位置
        tableModel.addColumn("纬度"); // 充电宝纬度
        tableModel.addColumn("经度"); // 充电宝经度
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

        // 设置表格列宽
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

        // 创建并添加添加充电宝按钮
        addButton = new JButton("添加充电宝");
        buttonPanel.add(addButton);

        // 创建并添加编辑充电宝按钮
        editButton = new JButton("编辑充电宝");
        buttonPanel.add(editButton);

        // 创建并添加删除充电宝按钮
        deleteButton = new JButton("删除充电宝");
        buttonPanel.add(deleteButton);

        // 创建并添加刷新按钮
        refreshButton = new JButton("刷新");
        buttonPanel.add(refreshButton);

        // 创建并添加返回按钮
        backButton = new JButton("返回管理员界面");
        buttonPanel.add(backButton);

        // 将按钮面板添加到主面板
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗口
        add(panel);

        // 加载充电宝数据到表格
        loadPowerbankData(tableModel);

        // 为添加充电宝按钮添加点击事件监听器
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 弹出添加充电宝对话框
                AddEditPowerbankDialog addDialog = new AddEditPowerbankDialog(ManagePowerbanksFrame.this, "添加充电宝", null);
                addDialog.setVisible(true);
                // 刷新表格数据
                loadPowerbankData(tableModel);
            }
        });

        // 为编辑充电宝按钮添加点击事件监听器
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取选中的充电宝信息
                int selectedRow = powerbankTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ManagePowerbanksFrame.this, "请选择要编辑的充电宝", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 获取充电宝ID
                int powerbankId = (int) powerbankTable.getValueAt(selectedRow, 0);
                String location = (String) powerbankTable.getValueAt(selectedRow, 1);
                double latitude = (double) powerbankTable.getValueAt(selectedRow, 2);
                double longitude = (double) powerbankTable.getValueAt(selectedRow, 3);
                int batteryLevel = (int) powerbankTable.getValueAt(selectedRow, 4);
                String status = (String) powerbankTable.getValueAt(selectedRow, 5);
                BigDecimal pricePerHour = (BigDecimal) powerbankTable.getValueAt(selectedRow, 6);

                // 创建充电宝对象
                Powerbank powerbank = new Powerbank(powerbankId, location, latitude, longitude, batteryLevel, status, pricePerHour);

                // 弹出编辑充电宝对话框
                AddEditPowerbankDialog editDialog = new AddEditPowerbankDialog(ManagePowerbanksFrame.this, "编辑充电宝", powerbank);
                editDialog.setVisible(true);
                // 刷新表格数据
                loadPowerbankData(tableModel);
            }
        });

        // 为删除充电宝按钮添加点击事件监听器
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取选中的充电宝ID和位置
                int selectedRow = powerbankTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ManagePowerbanksFrame.this, "请选择要删除的充电宝", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int powerbankId = (int) powerbankTable.getValueAt(selectedRow, 0);
                String location = (String) powerbankTable.getValueAt(selectedRow, 1);

                // 确认删除
                int confirm = JOptionPane.showConfirmDialog(ManagePowerbanksFrame.this, "确定要删除充电宝 ID：" + powerbankId + "，位置：" + location + " 吗？", "确认删除", JOptionPane.YES_NO_OPTION);
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
            }
        });

        // 为刷新按钮添加点击事件监听器
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 重新加载充电宝数据
                loadPowerbankData(tableModel);
            }
        });

        // 为返回按钮添加点击事件监听器
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 返回管理员界面
                AdminFrame adminFrame = new AdminFrame(username); // 传递用户名
                adminFrame.setVisible(true);
                // 关闭当前界面
                dispose();
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

                // 获取状态
                String dbStatus = rs.getString("status");
                String displayStatus;
                if (dbStatus.equalsIgnoreCase("available")) {
                    displayStatus = "可用";
                } else if (dbStatus.equalsIgnoreCase("unavailable")) {
                    displayStatus = "不可用";
                } else if (dbStatus.equalsIgnoreCase("maintenance")) {
                    displayStatus = "维修中";
                } else {
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
