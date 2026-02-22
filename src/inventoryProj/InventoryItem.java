package inventoryProj;

import java.time.LocalDate;

public class InventoryItem {
    private final String name;
    private final String brand;
    private final double purchasePrice;
    private final double salesPrice;
    private final int stock;
    private final LocalDate expiryDate;

    public InventoryItem(String name, String brand, double purchasePrice, double salesPrice,int stock, LocalDate expiryDate) {
        this.name = name;
        this.brand = brand;
        this.purchasePrice = purchasePrice;
        this.salesPrice = salesPrice;
        this.stock = stock;
        this.expiryDate = expiryDate;
    }

    public String getName() {
    	return name;
    }
    
    public String getBrand() { 
    	return brand;
    }
    
    public double getPurchasePrice() {
    	return purchasePrice;
    }
    
    public double getSalesPrice() {
    	return salesPrice;
    }
    
    public int getStock() { 
    	return stock;
    }
    
    public LocalDate getExpiryDate() {
    	return expiryDate;
    }
}
