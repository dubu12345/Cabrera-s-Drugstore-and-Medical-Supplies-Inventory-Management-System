package inventoryProj;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfReportGenerator extends AbstractReportGenerator {
    @Override
    public void generate(List<InventoryItem> items) throws DocumentException, java.io.IOException {
        String dateFmt = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        String base = "C:/Users/lynet/eclipse-workspace/InventoryProject/src/Report/inventory_report_" + dateFmt;
        int num = 1;
        String path = base + "_" + num + ".pdf";
        File f = new File(path);
        while (f.exists()) { num++; path = base + "_" + num + ".pdf"; f = new File(path); }

        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();
        Paragraph title = new Paragraph("CABRERA'S DRUGSTORE AND MEDICAL SUPPLIES");
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        String[] cols = {"NAMES","BRAND","PURCHASE PRICE","SALES PRICE","STOCK","EXPIRY DATE"};
        for (String c: cols) table.addCell(new Paragraph(c));
        for (InventoryItem it: items) {
            table.addCell(new Paragraph(it.getName()));
            table.addCell(new Paragraph(it.getBrand()));
            table.addCell(new Paragraph(String.valueOf(it.getPurchasePrice())));
            table.addCell(new Paragraph(String.valueOf(it.getSalesPrice())));
            table.addCell(new Paragraph(String.valueOf(it.getStock())));
            table.addCell(new Paragraph(it.getExpiryDate().toString()));
        }
        doc.add(table);
        doc.close();
        JOptionPane.showMessageDialog(null, "Report generated: " + path, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}