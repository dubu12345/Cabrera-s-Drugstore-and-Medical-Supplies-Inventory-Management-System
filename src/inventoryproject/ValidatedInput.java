package inventoryproject;

import java.time.LocalDate;

public class ValidatedInput {
    public String name;
    public String brand;
    public double purchasePrice;
    public double salesPrice;
    public int stock;
    public LocalDate expiryDate;

    public ValidatedInput(String name, String brand, double purchasePrice, double salesPrice, int stock, LocalDate expiryDate) {
        this.name = name;
        this.brand = brand;
        this.purchasePrice = purchasePrice;
        this.salesPrice = salesPrice;
        this.stock = stock;
        this.expiryDate = expiryDate;
    }
    
    
}