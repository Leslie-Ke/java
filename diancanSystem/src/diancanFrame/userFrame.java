package diancanFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class userFrame extends JFrame {
    private int userId;
    private JList<String> orderList;

    public userFrame(int userId) {
        this.userId = userId;
        setTitle("用户界面");
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("欢迎使用点餐系统，用户ID: " + userId);
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));

        // 显示餐品列表
        DefaultListModel<String> listModel = new DefaultListModel<>();
        orderList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(orderList);
        centerPanel.add(scrollPane);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        JButton orderButton = new JButton("购买餐品");
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buyDish();
            }
        });
        buttonPanel.add(orderButton);

        JButton viewOrderButton = new JButton("查看订单");
        viewOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewOrders();
            }
        });
        buttonPanel.add(viewOrderButton);

        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }

    private void buyDish() {
        try (Connection conn = Database.getConnection()) {
            // 查询餐品列表
            String query = "SELECT * FROM Dishes";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            DefaultListModel<String> listModel = new DefaultListModel<>();
            while (rs.next()) {
                int dishId = rs.getInt("dish_id");
                String dishName = rs.getString("dish_name");
                double price = rs.getDouble("price");
                String dishInfo = "ID: " + dishId + ", 名称: " + dishName + ", 价格: " + price;
                listModel.addElement(dishInfo);
            }
            rs.close();

            // 显示餐品列表供用户选择
            Vector<String> vector = new Vector<>();
            for (int i = 0; i < listModel.size(); i++) {
                vector.add(listModel.getElementAt(i));
            }
            JList<String> dishList = new JList<>(vector);

            JScrollPane scrollPane = new JScrollPane(dishList);
            JOptionPane.showMessageDialog(this, scrollPane, "请选择餐品", JOptionPane.PLAIN_MESSAGE);

            // 获取用户选择的餐品信息
            String selectedDish = dishList.getSelectedValue();
            if (selectedDish != null) {
                int dishId = extractDishId(selectedDish);
                if (dishId != -1) {
                    placeOrder(dishId);
                } else {
                    JOptionPane.showMessageDialog(this, "无法解析选定的餐品信息", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "购买餐品时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "未找到驱动程序：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

    }
    private int extractDishId(String selectedDish) {
        try {
            // 从字符串中找到 ID: 后的数字部分
            int index = selectedDish.indexOf("ID:");
            if (index == -1) {
                return -1; // 如果未找到 ID:，返回 -1 表示无效ID
            }

            String idStr = selectedDish.substring(index + 3).trim(); // 从 "ID:" 后开始截取，去除空格
            int dishId = Integer.parseInt(idStr.split(",")[0].trim()); // 以逗号分割，获取数字部分
            return dishId;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            return -1; // 解析失败返回 -1 表示无效ID
        }
    }


    private void placeOrder(int dishId) {
        try (Connection conn = Database.getConnection()) {
            // 插入订单信息
            String insertQuery = "INSERT INTO Orders (user_id, dish_id) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, dishId);
            int affectedRows = insertStmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "成功购买餐品！", "成功", JOptionPane.INFORMATION_MESSAGE);
                viewOrders(); // 刷新订单列表
            } else {
                JOptionPane.showMessageDialog(this, "购买餐品失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "购买餐品时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "未找到驱动程序：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void viewOrders() {
        try (Connection conn = Database.getConnection()) {
            // 查询用户的订单列表
            String query = "SELECT * FROM Orders WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            DefaultListModel<String> listModel = new DefaultListModel<>();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int dishId = rs.getInt("dish_id");
                String orderInfo = "订单ID: " + orderId + ", 餐品ID: " + dishId;
                listModel.addElement(orderInfo);
            }
            orderList.setModel(listModel);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "查询订单时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "未找到驱动程序：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }


}

