import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.print.*;



public class BillingUI extends JFrame {

    JTextField txtProduct, txtPrice, txtQty;
    JLabel totalLabel;
    JTable table;
    DefaultTableModel model;
    double total = 0;
    
    int billNumber=1;
    Connection con;

    public BillingUI(){

        connectDB();

        setTitle("Billing System");
        setSize(1000,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // BACKGROUND IMAGE
        ImageIcon bg = new ImageIcon(getClass().getResource("/images/1567674.jpg"));
        Image img = bg.getImage().getScaledInstance(1000,600,Image.SCALE_SMOOTH);
        bg = new ImageIcon(img);

        JLabel background = new JLabel(bg);
        background.setLayout(null);
        setContentPane(background);

        // HEADER
        JLabel header = new JLabel("BILLING SYSTEM",SwingConstants.CENTER);
        header.setBounds(20,20,940,60);
        header.setFont(new Font("Segoe UI",Font.BOLD,32));
        header.setOpaque(true);
        header.setBackground(new Color(13,71,161));
        header.setForeground(Color.WHITE);
        background.add(header);

        // PRODUCT NAME
        JLabel l1 = new JLabel("Product Name :");
        l1.setBounds(60,130,150,30);
        l1.setFont(new Font("Segoe UI",Font.BOLD,18));
        background.add(l1);

        txtProduct = new JTextField();
        txtProduct.setBounds(220,130,250,35);
        txtProduct.setFont(new Font("Segoe UI", Font.BOLD,18));
        background.add(txtProduct);

        // PRICE
        JLabel l2 = new JLabel("Price :");
        l2.setBounds(60,190,150,30);
        l2.setFont(new Font("Segoe UI",Font.BOLD,18));
        background.add(l2);

        txtPrice = new JTextField();
        txtPrice.setBounds(220,190,250,35);
        txtPrice.setFont(new Font("Segoe UI", Font.BOLD,18));
        background.add(txtPrice);
        // QUANTITY
        JLabel l3 = new JLabel("Quantity :");
        l3.setBounds(60,250,150,30);
        l3.setFont(new Font("Segoe UI",Font.BOLD,18));
        background.add(l3);

        txtQty = new JTextField();
        txtQty.setBounds(220,250,250,35);
        txtQty.setFont(new Font("Segoe UI", Font.BOLD,18));
        background.add(txtQty);

        // ADD PRODUCT BUTTON
        JButton addBtn = new JButton("Add Product");
        addBtn.setBounds(60,320,410,45);
        addBtn.setFont(new Font("Segoe UI",Font.BOLD,20));
        addBtn.setBackground(new Color(0,150,80));
        addBtn.setForeground(Color.WHITE);
        background.add(addBtn);

        // CLEAR BUTTON
        JButton clearBtn = new JButton("Clear");
        clearBtn.setBounds(60,390,180,40);
        clearBtn.setBackground(new Color(33,94,170));
        clearBtn.setForeground(Color.WHITE);
        background.add(clearBtn);

        // REMOVE BUTTON
        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setBounds(290,390,180,40);
        removeBtn.setBackground(new Color(255,183,0));
        background.add(removeBtn);

        // TABLE
        model = new DefaultTableModel();
        model.addColumn("Product");
        model.addColumn("Price");
        model.addColumn("Quantity");
        model.addColumn("Total");

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,16));
        table.getTableHeader().setBackground(new Color(33,94,170));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(520,130,420,300);
        background.add(sp);

        // TOTAL TEXT
        JLabel totalText = new JLabel("Total Amount :");
        totalText.setBounds(60,470,200,35);
        totalText.setFont(new Font("Segoe UI",Font.BOLD,24));
        background.add(totalText);

        // TOTAL BOX
        totalLabel = new JLabel("0.00",SwingConstants.CENTER);
        totalLabel.setBounds(260,465,200,45);
        totalLabel.setOpaque(true);
        totalLabel.setBackground(new Color(230,230,230));
        totalLabel.setFont(new Font("Segoe UI",Font.BOLD,26));
        totalLabel.setForeground(new Color(0,140,70));
        background.add(totalLabel);

        // GENERATE BILL BUTTON
        JButton billBtn = new JButton("Generate Bill");
        billBtn.setBounds(520,460,200,50);
        billBtn.setFont(new Font("Segoe UI",Font.BOLD,18));
        billBtn.setBackground(new Color(33,94,170));
        billBtn.setForeground(Color.WHITE);
        background.add(billBtn);

        // EXIT BUTTON
        JButton exitBtn = new JButton("Exit");
        exitBtn.setBounds(740,460,200,50);
        exitBtn.setFont(new Font("Segoe UI",Font.BOLD,18));
        exitBtn.setBackground(new Color(200,40,40));
        exitBtn.setForeground(Color.WHITE);
        background.add(exitBtn);
        
        //PRINT BUTTON
        JButton printBtn = new JButton("Print Bill");
        printBtn.setBounds(740,400,200,45);
        printBtn.setFont(new Font("Segoe UI",Font.BOLD,18));
        printBtn.setBackground(new Color(0,120,200));
        printBtn.setForeground(Color.WHITE);
        background.add(printBtn);
        
        // BUTTON ACTIONS
        addBtn.addActionListener(e -> addProduct());
        clearBtn.addActionListener(e -> clearFields());
        removeBtn.addActionListener(e -> removeRow());

        billBtn.addActionListener(e -> generateReceipt());

        exitBtn.addActionListener(e -> System.exit(0));
         printBtn.addActionListener(e -> printBill());

        setVisible(true);
    }

    // DATABASE CONNECTION
    void connectDB(){

        try{

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/billing_system",
            "root",
            "123456");

        }catch(Exception e){

            System.out.println(e);

        }

    }

    // ADD PRODUCT
    void addProduct(){

    try{

        String product = txtProduct.getText().trim();
        double price = Double.parseDouble(txtPrice.getText().trim());
        String qtyText = txtQty.getText().trim();

        String number = qtyText.replaceAll("[^0-9.]", "");
        double qty = Double.parseDouble(number);

        double subtotal = price * qty;

        model.addRow(new Object[]{product,price,qtyText,subtotal});

        // MYSQL SAVE
        PreparedStatement pst = con.prepareStatement(
        "INSERT INTO products(name,price,quantity,total) VALUES(?,?,?,?)"
        );

        pst.setString(1, product);
        pst.setDouble(2, price);
        pst.setDouble(3, qty);
        pst.setDouble(4, subtotal);

        pst.executeUpdate();

        total += subtotal;
        totalLabel.setText(String.format("%.2f",total));

        clearFields();

    }catch(Exception e){

        JOptionPane.showMessageDialog(this,"Error : "+e.getMessage());

    }

}
    // CLEAR FIELDS
    void clearFields(){

        txtProduct.setText("");
        txtPrice.setText("");
        txtQty.setText("");

    }

    // REMOVE ROW
    void removeRow(){

        int row = table.getSelectedRow();

        if(row >= 0){

            double val = Double.parseDouble(model.getValueAt(row,3).toString());

            total -= val;

            totalLabel.setText(String.format("%.2f",total));

            model.removeRow(row);

        }else{

            JOptionPane.showMessageDialog(this,"Please select a row");

        }

    }
    void generateReceipt(){

    StringBuilder bill = new StringBuilder();

    String dateTime = java.time.LocalDateTime.now().toString();

    bill.append("        MY SHOP BILL\n");
    bill.append("Bill No : "+billNumber+"\n");
    bill.append("Date : "+dateTime+"\n");
    bill.append("-----------------------------------\n");
    bill.append("Product\tPrice\tQty\tTotal\n");
    bill.append("-----------------------------------\n");

    for(int i=0;i<table.getRowCount();i++){

        String product = model.getValueAt(i,0).toString();
        String price = model.getValueAt(i,1).toString();
        String qty = model.getValueAt(i,2).toString();
        String subtotal = model.getValueAt(i,3).toString();

        bill.append(product+"\t"+price+"\t"+qty+"\t"+subtotal+"\n");

    }

    bill.append("-----------------------------------\n");
    bill.append("TOTAL = "+total+"\n");
    bill.append("-----------------------------------\n");
    bill.append("THANK YOU VISIT AGAIN\n");

    JTextArea area = new JTextArea(bill.toString());
    area.setFont(new Font("Monospaced",Font.BOLD,14));

    JOptionPane.showMessageDialog(this,new JScrollPane(area));

    billNumber++;
}
void printBill(){

    try{

        JTextArea area = new JTextArea();

        for(int i=0;i<table.getRowCount();i++){

            area.append(
            model.getValueAt(i,0)+"   "+
            model.getValueAt(i,1)+"   "+
            model.getValueAt(i,2)+"   "+
            model.getValueAt(i,3)+"\n");

        }

        area.append("\nTOTAL : "+total);

        area.print();

    }catch(Exception e){

        System.out.println(e);

    }

}
    public static void main(String[] args) {

        new BillingUI();

    }
}