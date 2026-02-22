package inventoryProj;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;


public class NearExpiryItemsPanelBuilder extends AbstractPanelBuilder {
    private final AbstractInventoryManager mgr;
    private JTable table; 
    
    public NearExpiryItemsPanelBuilder(AbstractInventoryManager mgr){this.mgr=mgr;}
    
    @Override
    public JPanel build(){
        JPanel p=new JPanel(new BorderLayout());
        JTable t=new JTable(new DefaultTableModel(
            new Object[][]{},
            new String[]{"NAMES","BRAND","PURCHASE PRICE","SALES PRICE","STOCK","EXPIRY DATE"}
        ));
        load(t);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }
    
    public void reload() {
        load(table);
    }
    
    private void load(JTable t){
        DefaultTableModel m=(DefaultTableModel)t.getModel();
        LocalDate now=LocalDate.now();
        try{
            for(InventoryItem it:mgr.loadItems())
                if(it.getExpiryDate().isAfter(now) && it.getExpiryDate().isBefore(now.plusDays(30)))
                    m.addRow(new Object[]{it.getName(), it.getBrand(), it.getPurchasePrice(), it.getSalesPrice(), it.getStock(), it.getExpiryDate()});
        } catch(Exception e){
            JOptionPane.showMessageDialog(null,"Error loading near expiry items","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
