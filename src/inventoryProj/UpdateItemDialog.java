package inventoryProj;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableModel;

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;

public class UpdateItemDialog extends JDialog {
 private final AbstractInventoryManager manager;
 private final JTable table;
 private final int rowIndex;
 private int oldStock;

 private JTextField nameField;
 private JTextField brandField;
 private JTextField purchasePriceField;
 private JTextField salesPriceField;
 private JTextField stockField;
 private JTextField expiryDateField;
 private JButton saveButton;
 private JButton cancelButton;

 public UpdateItemDialog(AbstractInventoryManager mgr, JTable table, int rowIndex) {
     super((Frame) null, "Update Item", true);
     this.manager = mgr;
     this.table = table;
     this.rowIndex = rowIndex;

     setSize(400, 350);
     setLocationRelativeTo(null);
     setDefaultCloseOperation(DISPOSE_ON_CLOSE);

     initUI();
 }

 private void initUI() {
     DefaultTableModel model = (DefaultTableModel) table.getModel();
     String currentName = model.getValueAt(rowIndex, 0).toString();
     String currentBrand = model.getValueAt(rowIndex, 1).toString();
     String currentPurchase = model.getValueAt(rowIndex, 2).toString();
     String currentSales = model.getValueAt(rowIndex, 3).toString();
     String stockText = model.getValueAt(rowIndex, 4).toString();
     String currentExpiry = model.getValueAt(rowIndex, 5).toString();

     oldStock = Integer.parseInt(stockText);

     nameField = new JTextField(currentName);
     brandField = new JTextField(currentBrand);
     purchasePriceField = new JTextField(currentPurchase);
     salesPriceField = new JTextField(currentSales);
     stockField = new JTextField(stockText);
     expiryDateField = new JTextField(currentExpiry);

     JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
     formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

     formPanel.add(new JLabel("Name:"));
     formPanel.add(nameField);
     formPanel.add(new JLabel("Brand:"));
     formPanel.add(brandField);
     formPanel.add(new JLabel("Purchase Price:"));
     formPanel.add(purchasePriceField);
     formPanel.add(new JLabel("Sales Price:"));
     formPanel.add(salesPriceField);
     formPanel.add(new JLabel("Stock:"));
     formPanel.add(stockField);
     formPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
     formPanel.add(expiryDateField);

     saveButton = new JButton("Save");
     cancelButton = new JButton("Cancel");
     Color btnBg = new Color(0x1F5F38);
     for (JButton b : new JButton[]{saveButton, cancelButton}) {
         b.setBackground(btnBg);
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
     String newName = nameField.getText().trim();
     String newBrand = brandField.getText().trim();
     String purchaseText = purchasePriceField.getText().trim();
     String salesText = salesPriceField.getText().trim();
     String stockText = stockField.getText().trim();
     String expiryText = expiryDateField.getText().trim();

     if (newName.isEmpty() || newBrand.isEmpty() || purchaseText.isEmpty() || salesText.isEmpty() || stockText.isEmpty()   || expiryText.isEmpty()) {
         JOptionPane.showMessageDialog(this, "All fields must be filled.","Input Error", JOptionPane.ERROR_MESSAGE);
         	return;
     }

     try {
         double newPurchase = Double.parseDouble(purchaseText);
         double newSales = Double.parseDouble(salesText);
         int newStock = Integer.parseInt(stockText);
         LocalDate newExpiry = LocalDate.parse(expiryText);

         List<InventoryItem> items = manager.loadItems();
         InventoryItem updated = new InventoryItem( newName, newBrand, newPurchase, newSales, newStock, newExpiry);
         items.set(rowIndex, updated);
         manager.saveItems(items);

         List<String[]> history = manager.loadHistory();
         String itemLabel = newName + " (" + newBrand + ")";
         String today     = LocalDate.now().toString();
         if (newStock > oldStock) {
             int addedQty = newStock - oldStock;
             history.add(new String[]{itemLabel, today, "Restock", String.valueOf(addedQty), "Details"});
         } else if (newStock == 0 && oldStock > 0) {
             history.add(new String[]{itemLabel, today, "Out of Stock", "0", "Details"});
         }
         manager.saveHistory(history);

         DefaultTableModel model = (DefaultTableModel) table.getModel();
         model.setValueAt(newName, rowIndex, 0);
         model.setValueAt(newBrand, rowIndex, 1);
         model.setValueAt(newPurchase, rowIndex, 2);
         model.setValueAt(newSales, rowIndex, 3);
         model.setValueAt(newStock, rowIndex, 4);
         model.setValueAt(expiryText, rowIndex, 5);

         dispose();
         
         SwingUtilities.invokeLater(() -> {
        	    // 'this' is the UpdateItemDialog instance:
        	    MainPage main = (MainPage)SwingUtilities.getAncestorOfClass(MainPage.class, this);
        	    if (main != null) {
        	        main.reloadAllPanels();
        	    }
        	});

         
     	} catch (NumberFormatException ex) {
     		JOptionPane.showMessageDialog(this, "Purchase Price, Sales Price, and Stock must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
         
     	} catch (Exception ex) { 
     		JOptionPane.showMessageDialog(this,"Error updating item: " + ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
     	}
 	}
}

