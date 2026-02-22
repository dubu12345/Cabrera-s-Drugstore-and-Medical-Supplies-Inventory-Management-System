package inventoryProj;

import java.util.List;

public abstract class AbstractInventoryManager {
    public abstract List<InventoryItem> loadItems() throws Exception;
    public abstract void saveItems(List<InventoryItem> items) throws Exception;
    public abstract List<String[]> loadSuppliers() throws Exception;
    public abstract List<String[]> loadHistory() throws Exception;
    public abstract void saveHistory(List<String[]> history) throws Exception;
	protected abstract void saveSuppliers(List<String[]> suppliers);
}
