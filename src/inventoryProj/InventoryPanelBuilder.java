package inventoryProj;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class InventoryPanelBuilder extends AbstractPanelBuilder {
    private final AbstractInventoryManager mgr;
    private JTable table;
    private JTextField searchField;

    public InventoryPanelBuilder(AbstractInventoryManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public JPanel build() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(249, 242, 232));

        JLabel titleLabel = new JLabel("INVENTORY");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton add = new JButton("Add");
        JButton upd = new JButton("Update");
        JButton del = new JButton("Delete");
        Stream.of(add, upd, del).forEach(b -> {
            b.setFont(new Font("Arial", Font.BOLD, 16));
            b.setBackground(new Color(31, 95, 56));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(add);
        buttonPanel.add(upd);
        buttonPanel.add(del);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        top.add(titleLabel, BorderLayout.WEST);
        top.add(searchPanel, BorderLayout.CENTER);
        top.add(buttonPanel, BorderLayout.SOUTH);

        table = new JTable(new DefaultTableModel(
            new Object[][]{},
            new String[]{"NAMES", "BRAND", "PURCHASE PRICE", "SALES PRICE", "STOCK", "EXPIRY DATE"}
        ));
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(table);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        loadItems();

        add.addActionListener(e -> new AddItemDialog(mgr, table).setVisible(true));
        upd.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a row to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                new UpdateItemDialog(mgr, table, row).setVisible(true);
            }
        });
        del.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a row to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                ((DefaultTableModel) table.getModel()).removeRow(row);
                saveItems();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String query = searchField.getText().toLowerCase();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                table.clearSelection();
                for (int i = 0; i < model.getRowCount(); i++) {
                    boolean match = false;
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        if (model.getValueAt(i, j).toString().toLowerCase().contains(query)) {
                            match = true;
                            break;
                        }
                    }
                    if (match) {
                        table.addRowSelectionInterval(i, i);
                    }
                }
            }
        });

        return panel;
    }

    private void loadItems() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            List<InventoryItem> items = mgr.loadItems();
            for (InventoryItem it : items) {
                model.addRow(new Object[]{
                    it.getName(),
                    it.getBrand(),
                    it.getPurchasePrice(),
                    it.getSalesPrice(),
                    it.getStock(),
                    it.getExpiryDate().toString()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error loading items: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveItems() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        List<InventoryItem> items = new java.util.ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            items.add(new InventoryItem(
                model.getValueAt(i, 0).toString(),
                model.getValueAt(i, 1).toString(),
                Double.parseDouble(model.getValueAt(i, 2).toString()),
                Double.parseDouble(model.getValueAt(i, 3).toString()),
                Integer.parseInt(model.getValueAt(i, 4).toString()),
                LocalDate.parse(model.getValueAt(i, 5).toString())
            ));
        }
        try {
            mgr.saveItems(items);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error saving items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
