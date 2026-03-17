import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BillingSystemDB {

    JFrame frame;
    JTextField txtProduct, txtPrice, txtQty;
    JButton btnAdd, btnClear, btnRemove, btnGenerate, btnExit;
    JTable table;
    JLabel lblTotal;

    DefaultTableModel model;
    int grandTotal = 0;

    Connection con;

    public BillingSystemDB() {
        // Connect to MySQL
        connectDB();

        // Frame Setup
        frame = new JFrame("Billing System");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setLocationRelativeTo(null); // Center

        // Title
        JLabel title = new JLabel("Billing System");
        title.setBounds(320, 10, 300, 40);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 102, 204));
        frame.add(title);

        // Labels and TextFields
        JLabel lblProduct = new JLabel("Product Name");
        lblProduct.setBounds(30, 70, 120, 25);
        frame.add(lblProduct);

        txtProduct = new JTextField();
        txtProduct.setBounds(150, 70, 150, 25);
        frame.add(txtProduct);

        JLabel lblPrice = new JLabel("Price");
        lblPrice.setBounds(30, 110, 120, 25);
        frame.add(lblPrice);

        txtPrice = new JTextField();
        txtPrice.setBounds(150, 110, 150, 25);
        frame.add(txtPrice);

        JLabel lblQty = new JLabel("Quantity");
        lblQty.setBounds(30, 150, 120, 25);
        frame.add(lblQty);

        txtQty = new JTextField();
        txtQty.setBounds(150, 150, 150, 25);
        frame.add(txtQty);

        // Buttons
        btnAdd = new JButton("Add Product");
        btnAdd.setBounds(50, 190, 250, 35);
        btnAdd.setBackground(new Color(0, 153, 76));
        btnAdd.setForeground(Color.WHITE);
        frame.add(btnAdd);

        btnClear = new JButton("Clear All");
        btnClear.setBounds(50, 240, 120, 35);
        btnClear.setBackground(new Color(204, 0, 0));
        btnClear.setForeground(Color.WHITE);
        frame.add(btnClear);

        btnRemove = new JButton("Remove Selected");
        btnRemove.setBounds(180, 240, 120, 35);
        btnRemove.setBackground(new Color(204, 102, 0));
        btnRemove.setForeground(Color.WHITE);
        frame.add(btnRemove);

        btnGenerate = new JButton("Generate Bill");
        btnGenerate.setBounds(50, 290, 120, 35);
        btnGenerate.setBackground(new Color(0, 102, 204));
        btnGenerate.setForeground(Color.WHITE);
        frame.add(btnGenerate);

        btnExit = new JButton("Exit");
        btnExit.setBounds(180, 290, 120, 35);
        btnExit.setBackground(new Color(102, 0, 102));
        btnExit.setForeground(Color.WHITE);
        frame.add(btnExit);

        // Table
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Product");
        model.addColumn("Price");
        model.addColumn("Quantity");
        model.addColumn("Total");

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(350, 70, 500, 300);
        frame.add(scroll);

        // Total Label
        lblTotal = new JLabel("Total = 0");
        lblTotal.setBounds(350, 380, 200, 30);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        frame.add(lblTotal);

        // Load existing products from DB
        loadProducts();

        // Button Actions
        btnAdd.addActionListener(e -> addProduct());
        btnClear.addActionListener(e -> clearAll());
        btnRemove.addActionListener(e -> removeSelected());
        btnGenerate.addActionListener(e -> generateBill());
        btnExit.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing_system", "root", "123456"); // replace password
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed!\n" + e.getMessage());
        }
    }

    void loadProducts() {
        try {
            model.setRowCount(0);
            grandTotal = 0;

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM products");

            while(rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                int qty = rs.getInt("quantity");
                int total = rs.getInt("total");

                model.addRow(new Object[]{id, name, price, qty, total});
                grandTotal += total;
            }
            lblTotal.setText("Total = " + grandTotal);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void addProduct() {
        try {
            String product = txtProduct.getText();
            int price = Integer.parseInt(txtPrice.getText());
            int qty = Integer.parseInt(txtQty.getText());
            int total = price * qty;

            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO products(name, price, quantity, total) VALUES(?,?,?,?)"
            );
            pst.setString(1, product);
            pst.setInt(2, price);
            pst.setInt(3, qty);
            pst.setInt(4, total);
            pst.executeUpdate();

            loadProducts();

            txtProduct.setText("");
            txtPrice.setText("");
            txtQty.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Enter valid values\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void clearAll() {
        model.setRowCount(0);
        grandTotal = 0;
        lblTotal.setText("Total = 0");
    }

    void removeSelected() {
        int selectedRow = table.getSelectedRow();
        if(selectedRow >= 0) {
            int id = (int) model.getValueAt(selectedRow, 0);

            try {
                PreparedStatement pst = con.prepareStatement("DELETE FROM products WHERE id=?");
                pst.setInt(1, id);
                pst.executeUpdate();
                loadProducts();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            JOptionPane.showMessageDialog(frame, "Select a row to remove", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    void generateBill() {
        JOptionPane.showMessageDialog(frame, "Bill Generated!\nTotal Amount = " + grandTotal);
    }

    public static void main(String[] args) {
        new BillingSystemDB();
    }
}