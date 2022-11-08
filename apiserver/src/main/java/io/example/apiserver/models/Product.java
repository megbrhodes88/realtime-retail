package io.example.apiserver.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product {
    @Id
    private String product_id;
    private String car_year;
    private String car_make;
    private String car_model;
    private String product_name;
    private int product_price;
    private String product_subtitle;
    private String product_desc;
    private int inventory = 0;
    private int discount = 0;

    public Product() {}

    public Product(String product_id, String car_year,  String car_make, String car_model, String product_name, int product_price, String product_subtitle, String product_desc, int inventory, int discount) {
        this.product_id = product_id;
        this.car_year = car_year;
        this.car_make = car_make;
        this.car_model = car_model;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_subtitle = product_subtitle;
        this.product_desc = product_desc;
        this.inventory = inventory;
        this.discount = discount;
    }

    public String getProductId() {
        return this.product_id;
    }
    public void setProductId(String product_id) {
        this.product_id = product_id;
    }
    public String getCarYear() {
        return this.car_year;
    }
    public void setCarYear(String car_year) {
        this.car_year = car_year;
    }
    public String getCarMake() {
        return this.car_make;
    }
    public void setCarMake(String car_make) {
        this.car_make = car_make;
    }
    public String getCarModel() {
        return this.car_model;
    }
    public void setCarModel(String car_model) {
        this.car_model = car_model;
    }
    public String getProductName() {
        return this.product_name;
    }
    public void setProductName(String product_name) {
        this.product_name = product_name;
    }
    public int getProductPrice() {
        return this.product_price;
    }
    public void setProductPrice(int product_price) {
        this.product_price = product_price;
    }
    public String getProductSubtitle() {
        return this.product_subtitle;
    }
    public void setProductSubtitle(String product_subtitle) {
        this.product_subtitle = product_subtitle;
    }
    public String getProductDesc() {
        return this.product_desc;
    }
    public void setProductDesc(String product_desc) {
        this.product_desc = product_desc;
    }
    public int getInventory() {
        return this.inventory;
    }
    public void setInventory(int inventory) {
        this.inventory = inventory;
    }
    public int getDiscount() {
        return this.discount;
    }
    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
