package io.example.apiserver.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProductSale {
    @Id
    private String product_id;
    private int discount;

    public ProductSale() {}

    public ProductSale(String product_id, int discount) {
        this.product_id = product_id;
        this.discount = discount;
    }

    public String getProductId() {
        return this.product_id;
    }
    public void setProductId(String product_id) {
        this.product_id = product_id;
    }
    public int getDiscount() {
        return this.discount;
    }
    public void setDiscont(int discount) {
        this.discount = discount; 
    }
}
