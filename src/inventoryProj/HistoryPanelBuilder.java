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

public class HistoryPanelBuilder extends AbstractPanelBuilder {
    private final AbstractInventoryManager mgr;
    private JTable table;

    public HistoryPanelBuilder(AbstractInventoryManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public JPanel build() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(249, 242, 232));

        JLabel titleLabel = new JLabel("ITEM HISTORY");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

        JButton deleteButton = new JButton("DELETE");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteButton.setBackground(new Color(31, 95, 56));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
        buttonPanel.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {},
            new String[]{"Item", "Date", "Action", "Qty", "Details"}
        );
        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        load(table);

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(
                    panel,
                    "Please select a history entry to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                panel,
                "Are you sure you want to delete this history entry?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                model.removeRow(row);
                saveHistoryFromTable(table);
            }
        });

        return panel;
    }
    
    public void reload() {
        load(table);
    }

    private void load(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            for (String[] h : mgr.loadHistory()) {
                model.addRow(h);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Error loading history",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void saveHistoryFromTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        List<String[]> history = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String[] row = new String[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                row[j] = model.getValueAt(i, j).toString();
            }
            history.add(row);
        }
        try {
            mgr.saveHistory(history);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Error saving history: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
