package diancanFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class adminFrame extends JFrame {

    private JList<String> userList;
    private JList<String> dishList;
    private JList<String> orderList;

    public adminFrame() {
        setTitle("管理员界面");
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("欢迎使用管理员界面");
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 3));

        // 用户列表
        DefaultListModel<String> userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        centerPanel.add(userScrollPane);

        // 餐品列表
        DefaultListModel<String> dishListModel = new DefaultListModel<>();
        dishList = new JList<>(dishListModel);
        JScrollPane dishScrollPane = new JScrollPane(dishList);
        centerPanel.add(dishScrollPane);

        // 订单列表
        DefaultListModel<String> orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        JScrollPane orderScrollPane = new JScrollPane(orderList);
        centerPanel.add(orderScrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1));

        JButton manageUsersButton = new JButton("管理用户");
        manageUsersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manageUsers();
            }
        });
        buttonPanel.add(manageUsersButton);

        JButton manageDishesButton = new JButton("管理餐品");
        manageDishesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manageDishes();
            }
        });
        buttonPanel.add(manageDishesButton);

        JButton viewOrdersButton = new JButton("查看订单");
        viewOrdersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewOrders();
            }
        });
        buttonPanel.add(viewOrdersButton);

        JButton addUserButton = new JButton("添加用户");
        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });
        buttonPanel.add(addUserButton);

        JButton addDishButton = new JButton("添加餐品");
        addDishButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDish();
            }
        });
        buttonPanel.add(addDishButton);

        JButton deleteDishButton = new JButton("删除餐品");
        deleteDishButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteDish();
            }
        });
        buttonPanel.add(deleteDishButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);

        // 初始化界面时显示用户列表和餐品列表
        manageUsers();
        manageDishes();
    }

    private void manageUsers() {
        try (Connection conn = Database.getConnection()) {
            // 查询用户列表
            String query = "SELECT * FROM Users";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            DefaultListModel<String> listModel = new DefaultListModel<>();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String role = rs.getString("role");
                String userInfo = "ID: " + userId + ", 用户名: " + username + ", 角色: " + role;
                listModel.addElement(userInfo);
            }
            userList.setModel(listModel);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "查询用户时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "未找到驱动程序：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void manageDishes() {
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
            dishList.setModel(listModel);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "查询餐品时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "未找到驱动程序：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void viewOrders() {
        try (Connection conn = Database.getConnection()) {
            // 查询所有订单列表
            String query = "SELECT * FROM Orders";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            DefaultListModel<String> listModel = new DefaultListModel<>();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int userId = rs.getInt("user_id");
                int dishId = rs.getInt("dish_id");
                String orderInfo = "订单ID: " + orderId + ", 用户ID: " + userId + ", 餐品ID: " + dishId;
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

    private void addUser() {
        String username = JOptionPane.showInputDialog(this, "请输入用户名：");
        if (username != null && !username.trim().isEmpty()) {
            String password = JOptionPane.showInputDialog(this, "请输入密码：");
            if (password != null && !password.trim().isEmpty()) {
                try (Connection conn = Database.getConnection()) {
                    // 添加用户
                    String insertQuery = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, password);
                    insertStmt.setString(3, "user"); // 默认为普通用户
                    int affectedRows = insertStmt.executeUpdate();

                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "用户添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        manageUsers(); // 刷新用户列表
                    } else {
                        JOptionPane.showMessageDialog(this, "用户添加失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "添加用户时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, "未找到驱动程序：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            } else {
                JOptionPane.showMessageDialog(this, "密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "用户名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDish() {
        String dishName = JOptionPane.showInputDialog(this, "请输入餐品名称：");
        if (dishName != null && !dishName.trim().isEmpty()) {
            String priceStr = JOptionPane.showInputDialog(this, "请输入餐品价格：");
            if (priceStr != null && !priceStr.trim().isEmpty()) {
                try {
                    double price = Double.parseDouble(priceStr);
                    try (Connection conn = Database.getConnection()) {
                        // 添加餐品
                        String insertQuery = "INSERT INTO Dishes (dish_name, price) VALUES (?, ?)";
                        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                        insertStmt.setString(1, dishName);
                        insertStmt.setDouble(2, price);
                        int affectedRows = insertStmt.executeUpdate();

                        if (affectedRows > 0) {
                            JOptionPane.showMessageDialog(this, "餐品添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                            manageDishes(); // 刷新餐品列表
                        } else {
                            JOptionPane.showMessageDialog(this, "餐品添加失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "添加餐品时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    } catch (ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(this, "未找到驱动程序：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "请输入有效的价格", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "价格不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "餐品名称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteDish() {
        // 获取选定的餐品信息
        String selectedDish = dishList.getSelectedValue();
        if (selectedDish == null) {
            JOptionPane.showMessageDialog(this, "请选择要删除的餐品", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 尝试从选定的餐品信息中提取餐品ID
        int dishId = extractDishId(selectedDish);
        if (dishId == -1) {
            JOptionPane.showMessageDialog(this, "无法解析选定的餐品信息", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 确认删除操作
        int choice = JOptionPane.showConfirmDialog(this, "确定要删除餐品ID为 " + dishId + " 的餐品吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection()) {
                // 删除餐品
                String deleteQuery = "DELETE FROM Dishes WHERE dish_id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                deleteStmt.setInt(1, dishId);
                int affectedRows = deleteStmt.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "餐品删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    manageDishes(); // 刷新餐品列表
                } else {
                    JOptionPane.showMessageDialog(this, "删除餐品失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "删除餐品时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "未找到驱动程序：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    // 辅助方法：从餐品信息字符串中提取餐品ID
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
}

