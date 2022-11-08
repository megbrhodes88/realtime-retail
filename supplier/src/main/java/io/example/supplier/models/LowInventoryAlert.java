package io.example.supplier.models;

public class LowInventoryAlert {
    private int availableInv;
    public LowInventoryAlert() {}
    public LowInventoryAlert(int availableInv) {
        this.availableInv = availableInv;
    }
    public int getAvailableInv() { return this.availableInv; }
    public void setAvailableInv(int availableInv) { this.availableInv = availableInv; }
}
