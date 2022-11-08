package io.example.supplier.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


public class Shipment {
    @JsonInclude(Include.NON_NULL)
    private String shipmentId;
    private List<ShipmentItem> shipmentItems;
    public Shipment() {}
    public Shipment(String shipmentId, List<ShipmentItem> shipmentItems) {
        this.shipmentId = shipmentId;
        this.shipmentItems = shipmentItems;
    }
    public String getShipmentId() { return this.shipmentId; }
    public List<ShipmentItem> getShipmentItems() { return this.shipmentItems; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
    public void setShipmentItems(List<ShipmentItem> shipmentItems) { this.shipmentItems = shipmentItems; }

    public static class ShipmentItem {
        @JsonInclude(Include.NON_NULL)
        private String productId;
        private int shipmentQuantity;
        public ShipmentItem() {}
        public ShipmentItem(String productId, int shipmentQuantity) {
            this.productId = productId;
            this.shipmentQuantity = shipmentQuantity;
        }
        public String getProductId() { return this.productId; }
        public int getShipmentQuantity() { return this.shipmentQuantity; }
        public void setProductId(String productId) { this.productId = productId; }
        public void setShipmentQuantity(int shipmentQuantity) { this.shipmentQuantity = shipmentQuantity; }
    }
}
