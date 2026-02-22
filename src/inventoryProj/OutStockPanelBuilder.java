package inventoryProj;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

public class OutStockPanelBuilder extends AbstractPanelBuilder {
    private final AbstractInventoryManager mgr;
    private JTable table; 

    public OutStockPanelBuilder(AbstractInventoryManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public JPanel build() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(249, 242, 232));

        JLabel titleLabel = new JLabel("OUT OF STOCK");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);

        JTable table = new JTable(new DefaultTableModel(
            new Object[][] {},
            new String[]{"NAMES","BRAND","PURCHASE PRICE","SALES PRICE","STOCK","EXPIRY DATE"}
        )) {

			private static final long serialVersionUID = 1L;

			@Override public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
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
                if (it.getStock() == 0) {
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
                "Error loading out-of-stock items",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
