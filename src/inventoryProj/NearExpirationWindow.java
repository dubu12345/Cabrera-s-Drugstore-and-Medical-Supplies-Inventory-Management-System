package inventoryProj;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class NearExpirationWindow extends JFrame {
    public NearExpirationWindow(AbstractInventoryManager mgr) {
        super("Items Near Expiration");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        JPanel panel = new NearExpiryItemsPanelBuilder(mgr).build();
        setContentPane(panel);
    }

    public static void showDialog(AbstractInventoryManager mgr) {
        SwingUtilities.invokeLater(() -> {
            NearExpirationWindow w = new NearExpirationWindow(mgr);
            w.setVisible(true);
        });
    }
}

