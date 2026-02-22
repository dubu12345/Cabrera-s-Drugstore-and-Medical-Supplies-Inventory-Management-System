package inventoryProj;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

public class LowStockWindow extends JFrame {
    private final AbstractInventoryManager manager;

    public LowStockWindow(AbstractInventoryManager manager) {
        this.manager = manager;
        setTitle("Low Stock Items");
        setSize(500, 400);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(249, 242, 232));

        JLabel label = new JLabel("Low Stock Items");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(label, BorderLayout.NORTH);

        JTable table = new JTable(new DefaultTableModel(
            new Object[][] {},
            new String[]{"NAMES","BRAND","PURCHASE PRICE","SALES PRICE","STOCK","EXPIRY DATE"}
        ));
        table.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        loadData(table);

        setContentPane(panel);
    }
    
    

    private void loadData(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            List<InventoryItem> items = manager.loadItems();
            for (InventoryItem it : items) {
                int stock = it.getStock();
                if (stock >= 1 && stock <= 50) {  // 1–50 inclusive is low stock
                    model.addRow(new Object[]{
                        it.getName(),
                        it.getBrand(),
                        it.getPurchasePrice(),
                        it.getSalesPrice(),
                        stock,
                        it.getExpiryDate().toString()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading low-stock items:\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
