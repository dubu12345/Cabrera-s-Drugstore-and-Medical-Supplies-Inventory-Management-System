package inventoryProj;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;

public class ExpiredItemsPanelBuilder extends AbstractPanelBuilder {
    private final AbstractInventoryManager mgr;
    private JTable table;

    public ExpiredItemsPanelBuilder(AbstractInventoryManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public JPanel build() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(249, 242, 232));

        JLabel titleLabel = new JLabel("EXPIRED ITEMS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

        JButton nearExpButton = new JButton("NEAR EXPIRATION");
        nearExpButton.setFont(new Font("Arial", Font.BOLD, 16));
        nearExpButton.setBackground(new Color(31, 95, 56));
        nearExpButton.setForeground(Color.WHITE);
        nearExpButton.setFocusPainted(false);
        
        nearExpButton.addActionListener(e -> {
            new NearExpirationWindow(mgr).setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
        buttonPanel.add(nearExpButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"NAMES","BRAND","PURCHASE PRICE","SALES PRICE","STOCK","EXPIRY DATE"}
        );
        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        load(table);

        return panel;
    }
    
    public void reload() {
        load(table);
    }

    private void load(JTable t) {
        DefaultTableModel m = (DefaultTableModel) t.getModel();
        m.setRowCount(0);
        LocalDate now = LocalDate.now();
        try {
            List<InventoryItem> items = mgr.loadItems();
            for (InventoryItem it : items) {
                if (it.getExpiryDate().isBefore(now)) {
                    m.addRow(new Object[]{
                        it.getName(),
                        it.getBrand(),
                        it.getPurchasePrice(),
                        it.getSalesPrice(),
                        it.getStock(),
                        it.getExpiryDate().toString()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Error loading expired items",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
