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
import java.util.ArrayList;
import java.util.List;

public class SupplierPanelBuilder extends AbstractPanelBuilder {
    private final AbstractInventoryManager mgr;

    public SupplierPanelBuilder(AbstractInventoryManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public JPanel build() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(249, 242, 232));

        JLabel titleLabel = new JLabel("SUPPLIER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

        JButton deleteBtn = new JButton("DELETE");
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 16));
        deleteBtn.setBackground(new Color(31, 95, 56));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
        buttonPanel.add(deleteBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, 
            new String[]{"Name", "Contact", "Address", "Email"}
        );
        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        load(table);

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(
                    panel,
                    "Please select a supplier to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                panel,
                "Are you sure you want to delete this supplier?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                model.removeRow(row);
                saveSuppliersFromTable(table);
            }
        });

        return panel;
    }

    private void load(JTable t) {
        DefaultTableModel model = (DefaultTableModel) t.getModel();
        model.setRowCount(0);
        try {
            for (String[] s : mgr.loadSuppliers()) {
                model.addRow(s);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Error loading suppliers",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void saveSuppliersFromTable(JTable t) {
        DefaultTableModel model = (DefaultTableModel) t.getModel();
        List<String[]> suppliers = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String[] row = new String[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                row[j] = model.getValueAt(i, j).toString();
            }
            suppliers.add(row);
        }
        try {
            mgr.saveSuppliers(suppliers);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Error saving suppliers: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
