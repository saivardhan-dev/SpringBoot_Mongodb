package com.restcalls.resttemplate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="orders")
public class Orders {

    @Id
    private String id;

    private String customerName;
    private String product;
    private String status;

    public Orders(String customerName, String product, String status) {
        this.customerName = customerName;
        this.product = product;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}



