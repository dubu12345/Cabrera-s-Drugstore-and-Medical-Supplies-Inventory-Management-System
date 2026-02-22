package inventoryProj;

import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

public class MainPage extends JFrame {
    private final AbstractInventoryManager manager;
    private final AbstractReportGenerator reportGen;
    private final CardLayout cardLayout;
    private final JPanel rightPanel;
    private final InventoryPanelBuilder invBuilder;
    private final InStockPanelBuilder    inBuilder;
    private final OutStockPanelBuilder   outBuilder;
    private final SupplierPanelBuilder   supBuilder;
    private final HistoryPanelBuilder    histBuilder;
    private final ExpiredItemsPanelBuilder expBuilder;
    private final NearExpiryItemsPanelBuilder nearBuilder;

    public MainPage() {
        this.manager = new FileInventoryManager();
        this.reportGen = new PdfReportGenerator();
        invBuilder  = new InventoryPanelBuilder(manager);
        inBuilder   = new InStockPanelBuilder(manager);
        outBuilder  = new OutStockPanelBuilder(manager);
        supBuilder  = new SupplierPanelBuilder(manager);
        histBuilder = new HistoryPanelBuilder(manager);
        expBuilder  = new ExpiredItemsPanelBuilder(manager);
        nearBuilder = new NearExpiryItemsPanelBuilder(manager);

        setTitle("Cabrera's Drugstore & Medical Supplies");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);
        initUI();

        watchInventoryFile();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                expiredItemsWarning();
                nearExpirationWarning();
                lowStockWarning();
                outOfStockWarning();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout(10,10));
        header.setBackground(new Color(248,202,35));
        header.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel iconTitle = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        iconTitle.setOpaque(false);

        JLabel icon = new JLabel(new ImageIcon(
            "C:/Users/lynet/OneDrive/Documents/NetBeansProjects/InventoryProject/src/img/icon70.png"
        ));
        iconTitle.add(icon);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel t1 = new JLabel("CABRERA'S DRUGSTORE");
        t1.setFont(new Font("Cambria",Font.BOLD,24));
        t1.setForeground(new Color(255,102,51));
        JLabel t2 = new JLabel("& MEDICAL SUPPLIES");
        t2.setFont(new Font("Arial",Font.BOLD,18));
        t2.setForeground(new Color(31,95,56));
        titles.add(t1);
        titles.add(t2);

        iconTitle.add(titles);
        header.add(iconTitle, BorderLayout.WEST);

        JButton rpt = new JButton("GENERATE REPORT");
        rpt.setFont(new Font("Arial",Font.BOLD,12));
        rpt.setBackground(new Color(249,242,232));
        rpt.addActionListener(e -> generateReport());
        header.add(rpt, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setBackground(new Color(255,102,51));
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(20,10,10,10));

        String[] labels = {"ITEMS","IN STOCK","OUT OF STOCK","SUPPLIER","ITEM HISTORY","EXPIRED"};
        for (String lbl : labels) {
            JButton b = createNavButton(lbl);
            nav.add(b);
            nav.add(Box.createVerticalStrut(10));
            b.addActionListener(e -> cardLayout.show(rightPanel, lbl));
        }
        nav.add(Box.createVerticalGlue());
        JButton logout = createNavButton("LOG OUT");
        logout.addActionListener(e -> System.exit(0));
        nav.add(logout);

        rightPanel.add(invBuilder.build(),  "ITEMS");
        rightPanel.add(inBuilder.build(),   "IN STOCK");
        rightPanel.add(outBuilder.build(),  "OUT OF STOCK");
        rightPanel.add(supBuilder.build(),  "SUPPLIER");
        rightPanel.add(histBuilder.build(), "ITEM HISTORY");
        rightPanel.add(expBuilder.build(),  "EXPIRED");
        rightPanel.add(nearBuilder.build(), "NEAR EXPIRATION");


        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, nav, rightPanel);
        split.setDividerLocation(200);
        add(split, BorderLayout.CENTER);
    }
    
    public void reloadAllPanels() {
        inBuilder.reload();
        outBuilder.reload();
        histBuilder.reload();
        expBuilder.reload();
        nearBuilder.reload();
    }


    private JButton createNavButton(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE,50));
        b.setFont(new Font("Arial",Font.BOLD,18));
        b.setBackground(new Color(31,95,56));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
    }

    private void generateReport() {
        try {
            List<InventoryItem> items = manager.loadItems();
            reportGen.generate(items);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error generating report: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void expiredItemsWarning() throws Exception {
        long count = manager.loadItems().stream()
            .filter(i -> i.getExpiryDate().isBefore(LocalDate.now()))
            .count();
        if (count > 0) {
            JOptionPane.showMessageDialog(this,
                "Some items have expired.",
                "Expired Warning",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void nearExpirationWarning() throws Exception {
        long count = manager.loadItems().stream()
            .filter(i -> {
                LocalDate d = i.getExpiryDate();
                return d.isAfter(LocalDate.now()) && d.isBefore(LocalDate.now().plusDays(30));
            })
            .count();
        if (count > 0) {
            JOptionPane.showMessageDialog(this,
                "Some items near expiration.",
                "Near Expiration Warning",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void lowStockWarning() throws Exception {
        long count = manager.loadItems().stream()
            .filter(i -> i.getStock() < 6)
            .count();
        if (count > 0) {
            JOptionPane.showMessageDialog(this,
                "Some items are low on stock.",
                "Low Stock Warning",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void outOfStockWarning() throws Exception {
        long count = manager.loadItems().stream()
            .filter(i -> i.getStock() == 0)
            .count();
        if (count > 0) {
            JOptionPane.showMessageDialog(this,
                "Some items are out of stock.",
                "Out of Stock Warning",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void watchInventoryFile() {
        try {
            Path file = Paths.get("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/inventoryData.txt");
            WatchService ws = FileSystems.getDefault().newWatchService();
            file.getParent().register(ws, StandardWatchEventKinds.ENTRY_MODIFY);
            Thread watcher = new Thread(() -> {
                try {
                    while (true) {
                        WatchKey key = ws.take();
                        for (WatchEvent<?> evt : key.pollEvents()) {
                            if (((Path)evt.context()).endsWith(file.getFileName())) {
                                
                            }
                        }
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            watcher.setDaemon(true);
            watcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainPage().setVisible(true));
    }
}
