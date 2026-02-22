package inventoryProj;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.List;

public class AddItemDialog extends JDialog {
 private final AbstractInventoryManager manager;
 private final JTable table;

 private JTextField nameField;
 private JTextField brandField;
 private JTextField purchasePriceField;
 private JTextField salesPriceField;
 private JTextField stockField;
 private JTextField expiryDateField;
 private JButton saveButton;
 private JButton cancelButton;

 public AddItemDialog(AbstractInventoryManager mgr, JTable table) {
     super((Frame) null, "Add New Item", true);
     this.manager = mgr;
     this.table = table;

     setSize(400, 350);
     setLocationRelativeTo(null);
     setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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
     restrictToNumbers(purchasePriceField);
     formPanel.add(purchasePriceField);

     formPanel.add(new JLabel("Sales Price:"));
     salesPriceField = new JTextField();
     restrictToNumbers(salesPriceField);
     formPanel.add(salesPriceField);

     formPanel.add(new JLabel("Stock:"));
     stockField = new JTextField();
     restrictToNumbers(stockField);
     formPanel.add(stockField);

     formPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
     expiryDateField = new JTextField();
     formPanel.add(expiryDateField);

     saveButton = new JButton("Save");
     cancelButton = new JButton("Cancel");
     Color buttonBg = new Color(0x1F5F38);
     for (JButton b : new JButton[]{saveButton, cancelButton}) {
         b.setBackground(buttonBg);
         b.setForeground(Color.WHITE);
         b.setFont(new Font("Arial", Font.PLAIN, 14));
         b.setFocusPainted(false);
     }
     formPanel.add(saveButton);
     formPanel.add(cancelButton);

     getContentPane().add(formPanel, BorderLayout.CENTER);

     saveButton.addActionListener(e -> onSave());
     cancelButton.addActionListener(e -> dispose());
 }

 private void onSave() {
     String nameText = nameField.getText().trim();
     String brandText = brandField.getText().trim();
     String purchaseText = purchasePriceField.getText().trim();
     String salesText = salesPriceField.getText().trim();
     String stockText = stockField.getText().trim();
     String expiryText = expiryDateField.getText().trim();

     if (nameText.isEmpty() || brandText.isEmpty() ||
         purchaseText.isEmpty() || salesText.isEmpty() ||
         stockText.isEmpty() || expiryText.isEmpty()) {
         JOptionPane.showMessageDialog(this,
             "All fields must be filled.",
             "Input Error",
             JOptionPane.ERROR_MESSAGE);
         return;
     }

     DefaultTableModel model = (DefaultTableModel) table.getModel();
     // Check duplicates
     for (int i = 0; i < model.getRowCount(); i++) {
         String existingName  = model.getValueAt(i, 0).toString();
         String existingBrand = model.getValueAt(i, 1).toString();
         if (existingName.equalsIgnoreCase(nameText) &&
             existingBrand.equalsIgnoreCase(brandText)) {
             JOptionPane.showMessageDialog(this,
                 "This item already exists in the inventory.",
                 "Duplicate Item",
                 JOptionPane.WARNING_MESSAGE);
             return;
         }
     }

     try {
         double purchase = Double.parseDouble(purchaseText);
         double sales    = Double.parseDouble(salesText);
         int stock       = Integer.parseInt(stockText);
         LocalDate expiry = LocalDate.parse(expiryText);

         List<InventoryItem> items = manager.loadItems();
         InventoryItem newItem = new InventoryItem(
             nameText, brandText, purchase, sales, stock, expiry
         );
         items.add(newItem);
         manager.saveItems(items);

         List<String[]> history = manager.loadHistory();
         String itemLabel = nameText + " (" + brandText + ")";
         String today     = LocalDate.now().toString();
         history.add(new String[]{ itemLabel, today, "Added", String.valueOf(stock), "Details" });
         manager.saveHistory(history);

         model.addRow(new Object[]{ nameText, brandText, purchase, sales, stock, expiryText });
         dispose();

     } catch (NumberFormatException ex) {
         JOptionPane.showMessageDialog(this,
             "Purchase Price, Sales Price, and Stock must be valid numbers.",
             "Input Error",
             JOptionPane.ERROR_MESSAGE);
     } catch (Exception ex) {
         JOptionPane.showMessageDialog(this,
             "Error saving item: " + ex.getMessage(),
             "Error",
             JOptionPane.ERROR_MESSAGE);
     }
 }

 private void restrictToNumbers(JTextField tf) {
     tf.addKeyListener(new KeyAdapter() {
         public void keyTyped(KeyEvent e) {
             char c = e.getKeyChar();
             if (!Character.isDigit(c) && c != '.' &&
                 c != KeyEvent.VK_BACK_SPACE &&
                 c != KeyEvent.VK_DELETE) {
                 e.consume();
             }
         }
     });
 }
}
