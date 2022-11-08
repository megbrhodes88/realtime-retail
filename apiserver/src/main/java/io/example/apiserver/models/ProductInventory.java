package io.example.apiserver.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProductInventory {
    @Id
    private String product_id;
    private int product_available_inv;
    private int product_reserved_inv;

    public ProductInventory() {}

    public ProductInventory(String product_id, int product_available_inv, int product_reserved_inv) {
        this.product_id = product_id;
        this.product_available_inv = product_available_inv;
        this.product_reserved_inv = product_reserved_inv;
    }

    public String getProductId() {
        return this.product_id;
    }
    public void setProductId(String product_id) {
        this.product_id = product_id;
    }
    public int getProductInvOnHand() {
        return this.product_available_inv;
    }
    public void setProductInvOnHand(int product_available_inv) {
        this.product_available_inv = product_available_inv;
    }
    public int getProductInvOnHold() {
        return this.product_reserved_inv;
    }
    public void setProductInvOnHold(int product_reserved_inv) {
        this.product_reserved_inv = product_reserved_inv;
    }
}
