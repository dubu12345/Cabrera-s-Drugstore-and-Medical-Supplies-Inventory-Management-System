package inventoryProj;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import inventoryProj.InventoryItem;

public class FileInventoryManager extends AbstractInventoryManager {
    private final Path invFile = Paths.get("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/inventoryData.txt");
    private final Path supFile = Paths.get("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/supplierData.txt");
    private final Path histFile= Paths.get("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/historyData.txt");

    @Override
    public List<InventoryItem> loadItems() throws IOException {
        List<InventoryItem> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(invFile)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split("\t");
                list.add(new InventoryItem(
                    d[0], d[1], Double.parseDouble(d[2]),
                    Double.parseDouble(d[3]), Integer.parseInt(d[4]),
                    LocalDate.parse(d[5])
                ));
            }
        }
        return list;
    }

    @Override
    public void saveItems(List<InventoryItem> items) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(invFile)) {
            bw.write("NAMES\tBRAND\tPURCHASE PRICE\tSALES PRICE\tSTOCK\tEXPIRY DATE"); bw.newLine();
            for (InventoryItem it : items) {
                bw.write(String.join("\t",
                    it.getName(), it.getBrand(),
                    String.valueOf(it.getPurchasePrice()),
                    String.valueOf(it.getSalesPrice()),
                    String.valueOf(it.getStock()),
                    it.getExpiryDate().toString()
                )); bw.newLine();
            }
        }
    }

    @Override
    public List<String[]> loadSuppliers() throws IOException {
        List<String[]> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(supFile)) {
            String l;
            while ((l = br.readLine()) != null) list.add(l.split("\t"));
        }
        return list;
    }

    @Override
    public List<String[]> loadHistory() throws IOException {
        List<String[]> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(histFile)) {
            String l;
            while ((l = br.readLine()) != null) list.add(l.split("\t"));
        }
        return list;
    }
    
    @Override
    public void saveSuppliers(List<String[]> suppliers) {
        Path path = Paths.get("C:/Users/lynet/eclipse-workspace/InventoryProject/src/Data/supplierData.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String[] s : suppliers) {
                writer.write(String.join("\t", s));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "Error saving suppliers: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    @Override
    public void saveHistory(List<String[]> history) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(histFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String[] row : history) {
                bw.write(String.join("\t", row)); bw.newLine();
            }
        }
    }
}