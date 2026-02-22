package inventoryproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import java.time.LocalDate;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat; 
import java.util.Date; 

public class MainSystem extends JFrame {

    private JPanel headerPanel;
    private JSplitPane splitPane;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private CardLayout cardLayout;
    private JButton itemButton, inStockButton, outStockButton, supplierButton, historyButton, expiredButton;
    private JButton reportButton, addButton;
    private JTable inventoryTable;
    private JTable inStockTable;
    private JTable outStockTable;
    private JTable expiredTable;
    private JTable historyTable;
    private JTable supplierTable;

    public MainSystem() {
        setTitle("Cabrera's Drugstore & Medical Supplies");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        initUI();
        loadTableData();
        loadSupplierData();
        watchInventoryFile();
        loadHistoryData();
        
        new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferExpiredItems();
            }
        }).start();
        
        new Thread(() -> {
            try {
                Thread.sleep(1000);  
                expiredItemsWarning();
                nearExpirationWarning();
                lowStockWarning(); 
                outOfStockWarning();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void initUI() {
    	
        // header panel
        headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(248, 202, 35));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // icon and title
        JPanel iconTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        iconTitlePanel.setOpaque(false);
        JLabel iconLabel = new JLabel(new ImageIcon("C:/Users/lynet/OneDrive/Documents/NetBeansProjects/InventoryProject/src/img/icon70.png")); 
        iconTitlePanel.add(iconLabel);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel title1 = new JLabel("CABRERA'S DRUGSTORE");
        title1.setFont(new Font("Cambria", Font.BOLD, 24));
        title1.setForeground(new Color(255, 102, 51));
        JLabel title2 = new JLabel("& MEDICAL SUPPLIES");
        title2.setFont(new Font("Arial", Font.BOLD, 18));
        title2.setForeground(new Color(31, 95, 56));
        titles.add(title1);
        titles.add(title2);
        iconTitlePanel.add(titles);
        headerPanel.add(iconTitlePanel, BorderLayout.WEST);

        reportButton = new JButton("GENERATE REPORT");
        reportButton.setFont(new Font("Arial", Font.BOLD, 12));
        reportButton.setBackground(new Color(249, 242, 232));
        headerPanel.add(reportButton, BorderLayout.EAST);
        
        reportButton.addActionListener(e -> generateReport());

        add(headerPanel, BorderLayout.NORTH);

        // left panel
        leftPanel = new JPanel();
        leftPanel.setBackground(new Color(255, 102, 51));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        itemButton = createNavButton("ITEMS");
        inStockButton = createNavButton("IN STOCK");
        outStockButton = createNavButton("OUT OF STOCK");
        supplierButton = createNavButton("SUPPLIER");
        historyButton = createNavButton("ITEM HISTORY");
        expiredButton = createNavButton("EXPIRED");

        leftPanel.add(itemButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(inStockButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(outStockButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(supplierButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(historyButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(expiredButton);
        
        // supplier action listener 
        supplierButton.addActionListener(e -> {
            cardLayout.show(rightPanel, "SUPPLIER");
        });
        
        // log out button
        
        JButton logOutButton = new JButton("LOG OUT");
        logOutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logOutButton.setBackground(new Color(31, 95, 56));
        logOutButton.setForeground(Color.WHITE);
        logOutButton.setFocusPainted(false);
        logOutButton.setBorderPainted(false);

        leftPanel.add(Box.createVerticalGlue()); 
        leftPanel.add(logOutButton);
        
        logOutButton.addActionListener(e -> {
            System.exit(0);  
        });

        // right panel with cardlayout
        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);

        // inventory panel 
        
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBackground(new Color(249, 242, 232));
        JLabel itemsLabel = new JLabel("INVENTORY");
        itemsLabel.setFont(new Font("Arial", Font.BOLD, 24));

        //add, update, delete button
        addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        Font btnFont = new Font("Arial", Font.BOLD, 16);
        Color btnColor = new Color(31, 95, 56);

        for (JButton btn : new JButton[]{addButton, updateButton, deleteButton}) {
            btn.setFont(btnFont);
            btn.setBackground(btnColor);
            btn.setForeground(Color.WHITE);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);        

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(200, 25));
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(itemsLabel, BorderLayout.NORTH); 
        topPanel.add(searchPanel, BorderLayout.CENTER); 

        JPanel buttonPanel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel1.setOpaque(false);
        buttonPanel1.add(addButton);
        buttonPanel1.add(updateButton);
        buttonPanel1.add(deleteButton);

        topPanel.add(buttonPanel1, BorderLayout.SOUTH);
        itemsPanel.add(topPanel, BorderLayout.NORTH);

        inventoryTable = new JTable(new DefaultTableModel(
            new Object[][]{},
            new String[]{"NAMES", "BRAND", "PURCHASE PRICE", "SALES PRICE", "STOCK", "EXPIRY DATE"}
        )) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        inventoryTable.getTableHeader().setReorderingAllowed(false);

        inventoryTable.getColumnModel().getColumn(0).setResizable(true);
        inventoryTable.getColumnModel().getColumn(1).setResizable(true);
        inventoryTable.getColumnModel().getColumn(2).setResizable(true);
        inventoryTable.getColumnModel().getColumn(3).setResizable(true);
        inventoryTable.getColumnModel().getColumn(4).setResizable(true);
        inventoryTable.getColumnModel().getColumn(5).setResizable(true);

        itemsPanel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        rightPanel.add(itemsPanel, "ITEMS");

        // in stock panel   
        
        JPanel inStockPanel = new JPanel(new BorderLayout());
        inStockPanel.setBackground(new Color(249, 242, 232));
        JLabel inStockTitleLabel = new JLabel("IN STOCK");
        inStockTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // low stock button
        JButton lowInStockButton = new JButton("LOW IN STOCK");
        lowInStockButton.setFont(new Font("Arial", Font.BOLD, 16));
        lowInStockButton.setBackground(new Color(31, 95, 56));
        lowInStockButton.setForeground(Color.WHITE);

        JPanel lowStockButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lowStockButtonPanel.setOpaque(false);
        lowStockButtonPanel.add(lowInStockButton);
 
        JPanel inStockTopPanel = new JPanel(new BorderLayout());
        inStockTopPanel.setOpaque(false);
        inStockTopPanel.add(inStockTitleLabel, BorderLayout.NORTH);
        inStockTopPanel.add(lowStockButtonPanel, BorderLayout.SOUTH);

        // in stock table 
        inStockTable = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"NAMES", "BRAND", "PURCHASE PRICE", "SALES PRICE", "STOCK", "EXPIRY DATE"}
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        
        inStockPanel.add(inStockTopPanel, BorderLayout.NORTH);
        inStockPanel.add(new JScrollPane(inStockTable), BorderLayout.CENTER);
        
        rightPanel.add(inStockPanel, "INSTOCK");	
        
        lowInStockButton.addActionListener(e -> {
            new LowStockWindow().setVisible(true);
        });
        
        // out stock panel

        rightPanel.add(createCard("OUT OF STOCK"), "OUTSTOCK");
        JPanel outStockPanel = new JPanel(new BorderLayout());
        outStockPanel.setBackground(new Color(249, 242, 232));

        JLabel outStockLabel = new JLabel("OUT OF STOCK");
        outStockLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // out stock table
        outStockTable = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"NAMES", "BRAND", "PURCHASE PRICE", "SALES PRICE", "STOCK", "EXPIRY DATE"}
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  
            }
        });

        JPanel outStockTopPanel = new JPanel(new BorderLayout());
        outStockTopPanel.setOpaque(false);
        outStockTopPanel.add(outStockLabel, BorderLayout.NORTH);


        outStockPanel.add(outStockTopPanel, BorderLayout.NORTH);
        outStockPanel.add(new JScrollPane(outStockTable), BorderLayout.CENTER);

        rightPanel.add(outStockPanel, "OUTSTOCK");

        // Supplier Panel
        
        JPanel supplierPanel = new JPanel(new BorderLayout());
        supplierPanel.setBackground(new Color(249, 242, 232));

        JLabel supplierLabel = new JLabel("SUPPLIER");
        supplierLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton deleteSupplierButton = new JButton("Delete");
        deleteSupplierButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteSupplierButton.setBackground(new Color(31, 95, 56));
        deleteSupplierButton.setForeground(Color.WHITE);
        deleteSupplierButton.addActionListener(e -> {
            int row = supplierTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                    "Please select a supplier to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this supplier?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ((DefaultTableModel) supplierTable.getModel()).removeRow(row);
            }
        });

        JPanel supHeader = new JPanel(new BorderLayout());
        supHeader.setOpaque(false);
        supHeader.add(supplierLabel, BorderLayout.WEST);
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrap.setOpaque(false);
        btnWrap.add(deleteSupplierButton);
        supHeader.add(btnWrap, BorderLayout.EAST);
        
        //supplier table
        supplierTable = new JTable(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Name", "Contact No.", "Address", "Email"}
        )) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        supplierTable.getTableHeader().setReorderingAllowed(false);
        supplierTable.setFillsViewportHeight(true);

        supplierPanel.add(supHeader, BorderLayout.NORTH);
        supplierPanel.add(new JScrollPane(supplierTable), BorderLayout.CENTER);

        rightPanel.add(supplierPanel, "SUPPLIER");

        // history panel 

        JPanel historyPanel = new JPanel(new BorderLayout(0, 0));  // Set vertical gap to 0
        historyPanel.setBackground(new Color(249, 242, 232));

        JPanel topPanel1 = new JPanel(new BorderLayout());
        topPanel1.setOpaque(false);

        JLabel historyLabel = new JLabel("ITEM HISTORY");
        historyLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton deleteHistoryButton = new JButton("DELETE");
        deleteHistoryButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteHistoryButton.setBackground(new Color(31, 95, 56));
        deleteHistoryButton.setForeground(Color.WHITE);

        JPanel deleteButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteButtonPanel.setOpaque(false);
        deleteButtonPanel.add(deleteHistoryButton);

        topPanel1.add(historyLabel, BorderLayout.WEST);
        topPanel1.add(deleteButtonPanel, BorderLayout.EAST); 

        historyPanel.add(topPanel1, BorderLayout.NORTH);

        historyTable = new JTable(new DefaultTableModel(
            new Object[][]{}, 
            new String[]{"Item", "Date", "Action", "Quantity", "Details"}
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are uneditable
            }
        });

        // Add the history table to the center of the historyPanel
        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // Add the historyPanel to the rightPanel
        rightPanel.add(historyPanel, "HISTORY");

        // Action listener for the Delete button
        deleteHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = historyTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MainSystem.this, "Please select a row to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Get the name of the item to be deleted (assuming it's in the first column)
                String itemNameToDelete = historyTable.getValueAt(selectedRow, 0).toString();

                // Remove the row from the history table
                DefaultTableModel historyModel = (DefaultTableModel) historyTable.getModel();
                historyModel.removeRow(selectedRow);

                // After deleting, save the updated history data
                saveHistoryData();  // Save data to the file after deletion

                // Remove the item from the history file
                removeItemFromHistoryFile(itemNameToDelete); // Remove from the history file
            }
        });

        // Expired Panel
        
        JPanel expiredPanel = new JPanel(new BorderLayout());
        expiredPanel.setBackground(new Color(249, 242, 232));

        JLabel expiredTitleLabel = new JLabel("EXPIRED ITEMS");
        expiredTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // expired table
        DefaultTableModel expiredModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"NAMES", "BRAND", "PURCHASE PRICE", "SALES PRICE", "STOCK", "EXPIRY DATE"}
        );
        expiredTable = new JTable(expiredModel);
        
        // button for near expiration
        JButton nearExpirationButton = new JButton("NEAR EXPIRATION");
        nearExpirationButton.setFont(new Font("Arial", Font.BOLD, 16));
        nearExpirationButton.setBackground(new Color(31, 95, 56));
        nearExpirationButton.setForeground(Color.WHITE);

        JPanel nearExpirationButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nearExpirationButtonPanel.setOpaque(false);
        nearExpirationButtonPanel.add(nearExpirationButton);

        JPanel expiredTopPanel = new JPanel(new BorderLayout());
        expiredTopPanel.setOpaque(false);
        expiredTopPanel.add(expiredTitleLabel, BorderLayout.NORTH);
        expiredTopPanel.add(nearExpirationButtonPanel, BorderLayout.SOUTH);

        expiredPanel.add(expiredTopPanel, BorderLayout.NORTH);
        expiredPanel.add(new JScrollPane(expiredTable), BorderLayout.CENTER);  // Add the expiredTable to the panel

        rightPanel.add(expiredPanel, "EXPIRED");

        nearExpirationButton.addActionListener(e -> {
            new NearExpirationWindow().setVisible(true);
        });

        // Splitpane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);

        // action listeners
        itemButton.addActionListener(e -> cardLayout.show(rightPanel, "ITEMS"));
        inStockButton.addActionListener(e -> cardLayout.show(rightPanel, "INSTOCK"));
        outStockButton.addActionListener(e -> cardLayout.show(rightPanel, "OUTSTOCK"));
        historyButton.addActionListener(e -> cardLayout.show(rightPanel, "HISTORY"));
        expiredButton.addActionListener(e -> cardLayout.show(rightPanel, "EXPIRED"));
        addButton.addActionListener(e -> new AddItem().setVisible(true));
        

        updateButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new UpdateItem(selectedRow).setVisible(true);

            refreshInStockTable();
          
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();
                String itemName = model.getValueAt(selectedRow, 0).toString();
                int stock = Integer.parseInt(model.getValueAt(selectedRow, 4).toString());

                model.removeRow(selectedRow);

                saveTableData();
                transferOutStockData();
                refreshOutStockTable();
            }
        });


        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                searchTable(query); 
            }
        });

        inStockButton.addActionListener(e -> {
            cardLayout.show(rightPanel, "INSTOCK");

            refreshInStockTable();
        });
    } 	

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(new Color(31, 95, 56));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private JPanel createCard(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(249, 242, 232));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);
        return panel;
    }
    
    private class AddItem extends JFrame {
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
            restrictToNumbers(purchasePriceField);

            formPanel.add(new JLabel("Sales Price:"));
            salesPriceField = new JTextField();
            formPanel.add(salesPriceField);
            restrictToNumbers(salesPriceField);

            formPanel.add(new JLabel("Stock:"));
            stockField = new JTextField();
            formPanel.add(stockField);
            restrictToNumbers(stockField);

            formPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
            expiryDateField = new JTextField();
            formPanel.add(expiryDateField);

            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");

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

            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String nameText = nameField.getText().trim();
                    String brandText = brandField.getText().trim();
                    String purchaseText = purchasePriceField.getText().trim();
                    String salesText = salesPriceField.getText().trim();
                    String stockText = stockField.getText().trim();
                    String expiryText = expiryDateField.getText().trim();

                    if (nameText.isEmpty() || brandText.isEmpty() || purchaseText.isEmpty()
                            || salesText.isEmpty() || stockText.isEmpty() || expiryText.isEmpty()) {
                        JOptionPane.showMessageDialog(AddItem.this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String existingName = model.getValueAt(i, 0).toString();
                        String existingBrand = model.getValueAt(i, 1).toString();
                        if (existingName.equalsIgnoreCase(nameText) && existingBrand.equalsIgnoreCase(brandText)) {
                            JOptionPane.showMessageDialog(AddItem.this, "This item already exists in the inventory.", "Duplicate Item", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

                    try {
                        double purchase = Double.parseDouble(purchaseText);
                        double sales = Double.parseDouble(salesText);
                        int stock = Integer.parseInt(stockText);

                        model.addRow(new Object[]{nameText, brandText, purchase, sales, stock, expiryText});
                        dispose();

                        updateHistoryTable("Added", stock, nameText, brandText);  
                        saveTableData();
                        loadTableData();

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(AddItem.this, "Purchase Price, Sales Price, and Stock must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            
            cancelButton.addActionListener(e -> dispose());
        }
    }
    
    private void restrictToNumbers(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); 
                }
            }
        });
    }

    private class UpdateItem extends JFrame {
        private JTextField nameField, brandField, purchasePriceField, salesPriceField, stockField, expiryDateField;
        private JButton saveButton, cancelButton;
        private int rowIndex;

        public UpdateItem(int rowIndex) {
            this.rowIndex = rowIndex;
            setTitle("Update Item");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(400, 350);
            setLocationRelativeTo(null);
            initUI();
        }

        private void initUI() {
            DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();

            int oldStock = Integer.parseInt(model.getValueAt(rowIndex, 4).toString());

            nameField           = new JTextField(model.getValueAt(rowIndex, 0).toString());
            brandField          = new JTextField(model.getValueAt(rowIndex, 1).toString());
            purchasePriceField  = new JTextField(model.getValueAt(rowIndex, 2).toString());
            salesPriceField     = new JTextField(model.getValueAt(rowIndex, 3).toString());
            stockField          = new JTextField(String.valueOf(oldStock));
            expiryDateField     = new JTextField(model.getValueAt(rowIndex, 5).toString());

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

            saveButton   = new JButton("Save");
            cancelButton = new JButton("Cancel");
            Color btnBg = new Color(0x1F5F38);
            for (JButton b : new JButton[]{saveButton, cancelButton}) {
                b.setBackground(btnBg);
                b.setForeground(Color.WHITE);
            }
            formPanel.add(saveButton);
            formPanel.add(cancelButton);

            add(formPanel, BorderLayout.CENTER);

            saveButton.addActionListener(e -> {
                if (nameField.getText().trim().isEmpty() ||
                    brandField.getText().trim().isEmpty() ||
                    purchasePriceField.getText().trim().isEmpty() ||
                    salesPriceField.getText().trim().isEmpty() ||
                    stockField.getText().trim().isEmpty() ||
                    expiryDateField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "All fields must be filled.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    String newName     = nameField.getText().trim();
                    String newBrand    = brandField.getText().trim();
                    double newPurchase = Double.parseDouble(purchasePriceField.getText().trim());
                    double newSales    = Double.parseDouble(salesPriceField.getText().trim());
                    int newStock       = Integer.parseInt(stockField.getText().trim());
                    String newExpiry   = expiryDateField.getText().trim();

                    model.setValueAt(newName,     rowIndex, 0);
                    model.setValueAt(newBrand,    rowIndex, 1);
                    model.setValueAt(newPurchase, rowIndex, 2);
                    model.setValueAt(newSales,    rowIndex, 3);
                    model.setValueAt(newStock,    rowIndex, 4);
                    model.setValueAt(newExpiry,   rowIndex, 5);

                    if (newStock > oldStock) {
                        int addedQty = newStock - oldStock;
                        updateHistoryTable("Restock", addedQty, newName, newBrand);
                    }

                    if (newStock == 0 && oldStock > 0) {
                        updateHistoryTable("Out of Stock", 0, newName, newBrand);
                    }

                    saveTableData();
                    refreshInStockTable();
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Purchase Price, Sales Price, and Stock must be valid numbers.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> dispose());
        }
    }
  
    private void searchTable(String query) {
        DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();

        inventoryTable.clearSelection();

        for (int i = 0; i < model.getRowCount(); i++) {
            boolean matches = false;
            for (int j = 0; j < model.getColumnCount(); j++) {
                String value = model.getValueAt(i, j).toString().toLowerCase();
                if (value.contains(query)) {
                    matches = true;
                    break;
                }
            }
            if (matches) {
                inventoryTable.addRowSelectionInterval(i, i);  
            }
        }
    }
    
    class LowStockWindow extends JFrame {
        private JTable lowStockTable;

        public LowStockWindow() { 
            setTitle("Low Stock Items");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(500, 400);
            setLocationRelativeTo(null);
            initUI();
        }

        private void initUI() {
            JPanel lowStockPanel = new JPanel(new BorderLayout());
            lowStockPanel.setBackground(new Color(249, 242, 232));

            JLabel lowStockLabel = new JLabel("Low Stock Items");
            lowStockLabel.setFont(new Font("Arial", Font.BOLD, 24));
            lowStockPanel.add(lowStockLabel, BorderLayout.NORTH);

            lowStockTable = new JTable(new DefaultTableModel(
                    new Object[][]{},
                    new String[]{"NAMES", "BRAND", "PURCHASE PRICE", "SALES PRICE", "STOCK", "EXPIRY DATE"}
            ) {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

            lowStockPanel.add(new JScrollPane(lowStockTable), BorderLayout.CENTER);
            
            add(lowStockPanel);
            populateLowStockTable();
            
        }

        private void populateLowStockTable() {
            DefaultTableModel model = (DefaultTableModel) lowStockTable.getModel();

            DefaultTableModel inventoryModel = (DefaultTableModel) inStockTable.getModel();

            for (int i = 0; i < inventoryModel.getRowCount(); i++) {
                int stock = Integer.parseInt(inventoryModel.getValueAt(i, 4).toString());
                if (stock >= 1 && stock <= 50) {  
                    Object[] row = new Object[inventoryModel.getColumnCount()];
                    for (int j = 0; j < inventoryModel.getColumnCount(); j++) {
                        row[j] = inventoryModel.getValueAt(i, j);  
                    }
                    model.addRow(row);
                }
            }
        }
        
        class UpdateLowStockItem extends JFrame {
            private JTextField nameField, brandField, purchasePriceField, salesPriceField, stockField, expiryDateField;
            private JButton saveButton, cancelButton;
            private int rowIndex;

            public UpdateLowStockItem(int rowIndex) {
                this.rowIndex = rowIndex;
                setTitle("Update Low Stock Item");
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setSize(400, 350);
                setLocationRelativeTo(null);
                initUI();
            }

            private void initUI() {
                JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
                formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                DefaultTableModel lowStockModel = (DefaultTableModel) lowStockTable.getModel();
                
                nameField = new JTextField(lowStockModel.getValueAt(rowIndex, 0).toString());
                brandField = new JTextField(lowStockModel.getValueAt(rowIndex, 1).toString());
                purchasePriceField = new JTextField(lowStockModel.getValueAt(rowIndex, 2).toString());
                salesPriceField = new JTextField(lowStockModel.getValueAt(rowIndex, 3).toString());
                stockField = new JTextField(lowStockModel.getValueAt(rowIndex, 4).toString());
                expiryDateField = new JTextField(lowStockModel.getValueAt(rowIndex, 5).toString());

                formPanel.add(new JLabel("Name:")); formPanel.add(nameField);
                formPanel.add(new JLabel("Brand:")); formPanel.add(brandField);
                formPanel.add(new JLabel("Purchase Price:")); formPanel.add(purchasePriceField);
                formPanel.add(new JLabel("Sales Price:")); formPanel.add(salesPriceField);
                formPanel.add(new JLabel("Stock:")); formPanel.add(stockField);
                formPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):")); formPanel.add(expiryDateField);

                saveButton = new JButton("Save");
                cancelButton = new JButton("Cancel");

                saveButton.setBackground(new Color(31, 95, 56));
                saveButton.setForeground(Color.WHITE);
                cancelButton.setBackground(new Color(31, 95, 56));
                cancelButton.setForeground(Color.WHITE);

                formPanel.add(saveButton);
                formPanel.add(cancelButton);

                add(formPanel, BorderLayout.CENTER);

                saveButton.addActionListener(e -> {
                    String updatedName = nameField.getText();
                    String updatedBrand = brandField.getText();
                    String updatedPurchasePrice = purchasePriceField.getText();
                    String updatedSalesPrice = salesPriceField.getText();
                    String updatedStock = stockField.getText();
                    String updatedExpiryDate = expiryDateField.getText();

                    lowStockModel.setValueAt(updatedName, rowIndex, 0);
                    lowStockModel.setValueAt(updatedBrand, rowIndex, 1);
                    lowStockModel.setValueAt(updatedPurchasePrice, rowIndex, 2);
                    lowStockModel.setValueAt(updatedSalesPrice, rowIndex, 3);
                    lowStockModel.setValueAt(updatedStock, rowIndex, 4);
                    lowStockModel.setValueAt(updatedExpiryDate, rowIndex, 5);

                    DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
                    inventoryModel.setValueAt(updatedName, rowIndex, 0);
                    inventoryModel.setValueAt(updatedBrand, rowIndex, 1);
                    inventoryModel.setValueAt(updatedPurchasePrice, rowIndex, 2);
                    inventoryModel.setValueAt(updatedSalesPrice, rowIndex, 3);
                    inventoryModel.setValueAt(updatedStock, rowIndex, 4);
                    inventoryModel.setValueAt(updatedExpiryDate, rowIndex, 5);

                    saveTableData();  

                    dispose();
                });

                cancelButton.addActionListener(e -> dispose());
            }
        }
    }
    
    
    class NearExpirationWindow extends JFrame {
        private JTable expiredTable;

        public NearExpirationWindow() {
            setTitle("Items Near Expiration");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(500, 400);
            setLocationRelativeTo(null);
            initUI();
        }

        private void initUI() {
            JPanel nearExpPanel = new JPanel(new BorderLayout());
            nearExpPanel.setBackground(new Color(249, 242, 232));

            JLabel nearExpLabel = new JLabel("Near Expiration Items");
            nearExpLabel.setFont(new Font("Arial", Font.BOLD, 24));
            nearExpPanel.add(nearExpLabel, BorderLayout.NORTH);

            expiredTable= new JTable(new DefaultTableModel(
                    new Object[][]{},
                    new String[]{"NAMES", "BRAND", "PURCHASE PRICE", "SALES PRICE", "STOCK", "EXPIRY DATE"}
            ));

            nearExpPanel.add(new JScrollPane(expiredTable), BorderLayout.CENTER);

            add(nearExpPanel);
            populateexpiredTable();
            transferExpiredItems();
        }

        private void populateexpiredTable() {
            DefaultTableModel model = (DefaultTableModel) expiredTable.getModel();

            DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();

            for (int i = 0; i < inventoryModel.getRowCount(); i++) {
                String expiryDate = inventoryModel.getValueAt(i, 5).toString();
                LocalDate expiry = LocalDate.parse(expiryDate);

                LocalDate currentDate = LocalDate.now();
                if (expiry.isBefore(currentDate.plusDays(30)) && expiry.isAfter(currentDate)) {
                    Object[] row = new Object[inventoryModel.getColumnCount()];
                    for (int j = 0; j < inventoryModel.getColumnCount(); j++) {
                        row[j] = inventoryModel.getValueAt(i, j);
                    }
                    model.addRow(row);
                }
            }
        }
    }
  
    private void transferInStockData() {
        DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
        DefaultTableModel inStockModel = (DefaultTableModel) inStockTable.getModel();

        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            String stock = inventoryModel.getValueAt(i, 4).toString();  
            
            if (!stock.isEmpty() && Integer.parseInt(stock) > 0) {
                Object[] row = new Object[inventoryModel.getColumnCount()];
                for (int j = 0; j < inventoryModel.getColumnCount(); j++) {
                    row[j] = inventoryModel.getValueAt(i, j); 
                }
                inStockModel.addRow(row);
            }
        }
       
    }
    
    private void transferOutStockData() {
        DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
        DefaultTableModel outStockModel = (DefaultTableModel) outStockTable.getModel();

        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            String stock = inventoryModel.getValueAt(i, 4).toString();

            if (stock.isEmpty() || Integer.parseInt(stock) == 0) {
                boolean exists = false;
                for (int j = 0; j < outStockModel.getRowCount(); j++) {
                    String name = outStockModel.getValueAt(j, 0).toString();
                    if (name.equals(inventoryModel.getValueAt(i, 0).toString())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    Object[] row = new Object[inventoryModel.getColumnCount()];
                    for (int j = 0; j < inventoryModel.getColumnCount(); j++) {
                        row[j] = inventoryModel.getValueAt(i, j);
                    }
                    outStockModel.addRow(row);
                }
            }
        }
        
    }
    
    private void transferExpiredItems() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
                DefaultTableModel expiredModel = (DefaultTableModel) expiredTable.getModel();

                expiredModel.setRowCount(0);

                for (int i = 0; i < inventoryModel.getRowCount(); i++) {
                    String expiryDateStr = inventoryModel.getValueAt(i, 5).toString();
                    LocalDate expiryDate = LocalDate.parse(expiryDateStr);

                    LocalDate currentDate = LocalDate.now();

                    if (expiryDate.isBefore(currentDate)) {
                        Object[] row = new Object[inventoryModel.getColumnCount()];
                        for (int j = 0; j < inventoryModel.getColumnCount(); j++) {
                            row[j] = inventoryModel.getValueAt(i, j);
                        }
                        expiredModel.addRow(row);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                ((DefaultTableModel) expiredTable.getModel()).fireTableDataChanged();
            }
        }.execute();
    }
    
    private void saveTableData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/inventoryData.txt"))) {
            DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();

            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) writer.write("\t");
            }
            writer.newLine();

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    writer.write(model.getValueAt(i, j).toString());
                    if (j < model.getColumnCount() - 1) writer.write("\t");
                }
                writer.newLine();
            }

            refreshInStockTable();
            refreshOutStockTable();
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data to file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateHistoryTable(String action, int quantity, String name, String brand) {
        DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
        DefaultTableModel historyModel = (DefaultTableModel) historyTable.getModel();
        String formattedItemName = name + " (" + brand + ")";
        LocalDate currentDate = LocalDate.now();
        Object[] historyRow = new Object[]{
            formattedItemName,  
            currentDate.toString(),  
            action,  
            quantity,  
            "Details"  
        };
        historyModel.addRow(historyRow);
        saveHistoryData();  
    }


    
    private void saveInStockData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/path/to/inStockData.txt"))) {
            DefaultTableModel model = (DefaultTableModel) inStockTable.getModel();

            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) writer.write("\t");
            }
            writer.newLine();

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    writer.write(model.getValueAt(i, j).toString());
                    if (j < model.getColumnCount() - 1) writer.write("\t");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving in-stock data to file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveHistoryData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/historyData.txt", false))) {
            DefaultTableModel historyModel = (DefaultTableModel) historyTable.getModel();
            for (int i = 0; i < historyModel.getRowCount(); i++) {
                for (int j = 0; j < historyModel.getColumnCount(); j++) {
                    writer.write(historyModel.getValueAt(i, j).toString());
                    if (j < historyModel.getColumnCount() - 1) writer.write("\t");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving history data to file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadHistoryData() {
        DefaultTableModel historyModel = (DefaultTableModel) historyTable.getModel();
        
        historyModel.setRowCount(0);
        
        try (BufferedReader reader = new BufferedReader(new FileReader("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/historyData.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");  
                historyModel.addRow(data);  
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading history data from file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeItemFromHistoryFile(String itemNameToDelete) {
        File historyFile = new File("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/historyData.txt");
        File tempFile = new File("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/tempHistoryData.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(historyFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                String itemName = data[0]; 

                if (!itemName.equals(itemNameToDelete)) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            if (historyFile.delete()) {
                if (tempFile.renameTo(historyFile)) {
                    System.out.println("History file updated successfully.");
                } else {
                    System.out.println("Failed to rename temp file.");
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating history file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshInStockTable() {
        DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
        DefaultTableModel inStockModel = (DefaultTableModel) inStockTable.getModel();

        inStockModel.setRowCount(0);  

        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            String stock = inventoryModel.getValueAt(i, 4).toString(); 
            if (!stock.isEmpty() && Integer.parseInt(stock) > 0) {
                Object[] row = new Object[inventoryModel.getColumnCount()];
                for (int j = 0; j < inventoryModel.getColumnCount(); j++) {
                    row[j] = inventoryModel.getValueAt(i, j); 
                }
                inStockModel.addRow(row);
            }
        }
    }

    private void refreshOutStockTable() {
        DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
        DefaultTableModel outStockModel = (DefaultTableModel) outStockTable.getModel();

        outStockModel.setRowCount(0); 

        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            String stock = inventoryModel.getValueAt(i, 4).toString();  
            if (stock.isEmpty() || Integer.parseInt(stock) == 0) {
                Object[] row = new Object[inventoryModel.getColumnCount()];
                for (int j = 0; j < inventoryModel.getColumnCount(); j++) {
                    row[j] = inventoryModel.getValueAt(i, j); 
                }
                outStockModel.addRow(row); 
            }
        }
    }
 
    private void loadTableData() {
        DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();

        try (BufferedReader reader = new BufferedReader(new FileReader("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/inventoryData.txt"))) {
            String line;

            model.setRowCount(0);

            reader.readLine(); 

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                model.addRow(data);
            }
            transferInStockData();
            transferOutStockData();
            transferExpiredItems();  
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading data from file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSupplierData() {
        DefaultTableModel supplierModel = (DefaultTableModel) supplierTable.getModel();
        
        supplierModel.setRowCount(0);
        
        try (BufferedReader reader = new BufferedReader(new FileReader("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/supplierData.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t"); 
                supplierModel.addRow(data); 
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading supplier data from file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // thread for out of stock warning
    private void outOfStockWarning() {
        DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
        int outOfStockCount = 0;

        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            String stock = inventoryModel.getValueAt(i, 4).toString();

            try {
                int stockValue = Integer.parseInt(stock);

                if (stockValue == 0) {
                    outOfStockCount++;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid stock value at row " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (outOfStockCount > 0) {
            JOptionPane.showMessageDialog(this, "Some items are out of stock. Check in the out of stock table.", "Out of Stock Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // thread for low stock warning
    private void lowStockWarning() {
        DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
        int lowStockCount = 0;

        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            String stock = inventoryModel.getValueAt(i, 4).toString();

            try {
                int stockValue = Integer.parseInt(stock);

                if (stockValue < 6) {
                    lowStockCount++;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid stock value at row " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (lowStockCount > 0) {
            JOptionPane.showMessageDialog(this, "Warning: Some items are low on stock. Check the in-stock table to identify.", "Low Stock Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    

    // thread for near expiration
    private void nearExpirationWarning() {
        DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
        int nearExpirationCount = 0;

        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            String expiryDateStr = inventoryModel.getValueAt(i, 5).toString();  
            LocalDate expiryDate = LocalDate.parse(expiryDateStr);

            LocalDate currentDate = LocalDate.now();
            if (expiryDate.isBefore(currentDate.plusDays(30)) && expiryDate.isAfter(currentDate)) {
                nearExpirationCount++;
            }
        }

        if (nearExpirationCount > 0) {
            JOptionPane.showMessageDialog(this, "Some items are near expiration. Please check the expired items.", "Near Expiration Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    // thread for expired items warning
    private void expiredItemsWarning() {
        DefaultTableModel inventoryModel = (DefaultTableModel) inventoryTable.getModel();
        int expiredItemCount = 0;
        
        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            String expiryDateStr = inventoryModel.getValueAt(i, 5).toString(); 
            LocalDate expiryDate = LocalDate.parse(expiryDateStr);

            LocalDate currentDate = LocalDate.now();
            if (expiryDate.isBefore(currentDate)) {
                expiredItemCount++;
            }
        }

        if (expiredItemCount > 0) {
            JOptionPane.showMessageDialog(this, "Some items have expired. Please check the expired items .", "Expired Items Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void watchInventoryFile() {
        Path inventoryFile = Paths.get("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/inventoryData.txt");
        Path dir = inventoryFile.getParent();

        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            Thread watcherThread = new Thread(() -> {
                try {
                    while (true) {
                        WatchKey key = watcher.take();
                        for (WatchEvent<?> evt : key.pollEvents()) {
                            Path changed = (Path)evt.context();
                            if (changed.equals(inventoryFile.getFileName())) {
                                // reload on Swing’s Event Dispatch Thread
                                SwingUtilities.invokeLater(this::loadTableData);
                            }
                        }
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            watcherThread.setDaemon(true);
            watcherThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateReport() {
        String dateFormat = new SimpleDateFormat("MM-dd-yyyy").format(new Date());

        String baseFileName = "C:/Users/lynet/eclipse-workspace/InventoryProject/src/Report/inventory_report_" + dateFormat;

        int fileNumber = 1;
        String pdfPath = baseFileName + "_" + fileNumber + ".pdf";
        
        File file = new File(pdfPath);
        while (file.exists()) {
            fileNumber++;
            pdfPath = baseFileName + "_" + fileNumber + ".pdf";
            file = new File(pdfPath);
        }
        
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();

            Paragraph title = new Paragraph("CABRERA'S DRUGSTORE AND MEDICAL SUPPLIES");
            title.setAlignment(Element.ALIGN_CENTER); 
            document.add(title);
            
            document.add(new Paragraph(" ")); 
            
            PdfPTable pdfTable = new PdfPTable(inventoryTable.getColumnCount());
            pdfTable.setWidthPercentage(100); 

            for (int i = 0; i < inventoryTable.getColumnCount(); i++) {
                pdfTable.addCell(new Paragraph(inventoryTable.getColumnName(i)));
            }

            DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    pdfTable.addCell(new Paragraph(model.getValueAt(i, j).toString()));
                }
            }

            document.add(pdfTable);
            
            document.close();
            JOptionPane.showMessageDialog(this, "Report generated successfully: " + pdfPath, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainSystem().setVisible(true));
    }
    
}
