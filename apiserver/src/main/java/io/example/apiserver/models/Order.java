package io.example.apiserver.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Order {
    @JsonInclude(Include.NON_NULL)
    private String orderId;
    private List<OrderItem> orderItems;
    public Order() {}
    public Order(String orderId, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.orderItems = orderItems;
    }
    public String getOrderId() { return this.orderId; }
    public List<OrderItem> getOrderItems() { return this.orderItems; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public static class OrderItem {
        @JsonInclude(Include.NON_NULL)
        private String productId;
        private int quantityPurchased;
        public OrderItem() {}
        public OrderItem(String productId, int quantityPurchased) {
            this.productId = productId;
            this.quantityPurchased = quantityPurchased;
        }
        public String getProductId() { return this.productId; }
        public int getQuantityPurchased() { return this.quantityPurchased; }
        public void setProductId(String productId) { this.productId = productId; }
        public void setQuantityPurchased(int quantityPurchased) { this.quantityPurchased = quantityPurchased; }
    }
}