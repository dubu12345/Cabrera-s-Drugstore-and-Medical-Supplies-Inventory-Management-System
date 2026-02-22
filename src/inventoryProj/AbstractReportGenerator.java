package inventoryProj;

import java.util.List;

public abstract class AbstractReportGenerator {
    public abstract void generate(List<InventoryItem> items) throws Exception;
}