package inventoryproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddItem extends JFrame {
    private JTextField nameField;
    private JTextField brandField;
    private JTextField purchasePriceField;
    private JTextField salesPriceField;
    private JTextField stockField;
    private JTextField expiryDateField;
    private JButton saveButton;
    private JButton cancelButton;

    public AddItem() {
        setTitle("Add New Item");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Brand:"));
        brandField = new JTextField();
        formPanel.add(brandField);

        formPanel.add(new JLabel("Purchase Price:"));
        purchasePriceField = new JTextField();
        formPanel.add(purchasePriceField);

        formPanel.add(new JLabel("Sales Price:"));
        salesPriceField = new JTextField();
        formPanel.add(salesPriceField);

        formPanel.add(new JLabel("Stock:"));
        stockField = new JTextField();
        formPanel.add(stockField);

        formPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
        expiryDateField = new JTextField();
        formPanel.add(expiryDateField);

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        // Style buttons: background #1F5F38, Arial font, white text
        Color buttonBg = new Color(0x1F5F38);
        saveButton.setBackground(buttonBg);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.PLAIN, 14));
        saveButton.setFocusPainted(false);
        cancelButton.setBackground(buttonBg);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setFocusPainted(false);

        formPanel.add(saveButton);
        formPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);

        // Action listeners
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: handle save logic, e.g., validate and pass data back to main table
                String name = nameField.getText();
                String brand = brandField.getText();
                String purchasePrice = purchasePriceField.getText();
                String salesPrice = salesPriceField.getText();
                String stock = stockField.getText();
                String expiryDate = expiryDateField.getText();
                // Example: System.out.println or callback
                System.out.println("Saved: " + name + ", " + brand + ", " + purchasePrice + ", " + salesPrice + ", " + stock + ", " + expiryDate);
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddItem().setVisible(true));
    }
}
