package com.restcalls.resttemplate.inventory;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventory")
public class Inventory {

    @Id
    private String id;

    private String productName;
    private String location;
    private String availability;

    public Inventory(String productName,  String location, String availability) {
        this.productName = productName;
        this.location = location;
        this.availability = availability;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductName() {return productName;}
    public void setProductName(String productName) {this.productName = productName;}

    public String getLocation() {return location;}
    public void setLocation(String location) { this.location = location; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

}
