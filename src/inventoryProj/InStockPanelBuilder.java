package inventoryProj;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

public class InStockPanelBuilder extends AbstractPanelBuilder {
    private final AbstractInventoryManager mgr;
    private JTable table; 

    public InStockPanelBuilder(AbstractInventoryManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public JPanel build() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(249, 242, 232));

        JLabel titleLabel = new JLabel("IN STOCK");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

        JButton lowStockButton = new JButton("LOW IN STOCK");
        lowStockButton.setFont(new Font("Arial", Font.BOLD, 16));
        lowStockButton.setBackground(new Color(31, 95, 56));
        lowStockButton.setForeground(Color.WHITE);
        lowStockButton.setFocusPainted(false);

        lowStockButton.addActionListener(e ->
            new LowStockWindow(mgr).setVisible(true)
        );

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
        buttonPanel.add(lowStockButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JTable table = new JTable(new DefaultTableModel(
            new Object[][] {},
            new String[]{"NAMES","BRAND","PURCHASE PRICE","SALES PRICE","STOCK","EXPIRY DATE"}
        ));
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(table);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        load(table);

        return panel;
    }
    
    public void reload() {
        load(table);
    }

    private void load(JTable t) {
        DefaultTableModel model = (DefaultTableModel) t.getModel();
        model.setRowCount(0);
        try {
            List<InventoryItem> items = mgr.loadItems();
            for (InventoryItem it : items) {
                if (it.getStock() > 0) {
                    model.addRow(new Object[]{
                        it.getName(),
                        it.getBrand(),
                        it.getPurchasePrice(),
                        it.getSalesPrice(),
                        it.getStock(),
                        it.getExpiryDate().toString()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
              null,
              "Error loading in-stock items",
              "Error",
              JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
