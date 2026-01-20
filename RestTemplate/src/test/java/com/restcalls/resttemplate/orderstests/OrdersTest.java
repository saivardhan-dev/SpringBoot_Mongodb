package com.restcalls.resttemplate.orderstests;


import com.restcalls.resttemplate.orders.Orders;
import org.junit.jupiter.api.Test;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrdersTest {

    @Test
    void checkAllFieldsAreStrings() {
        String id = "asdflkj";
        Orders order = new Orders("Kai", "Laptop", "Delivered");
        assertNotNull(id);
        assertNotNull(order.getCustomerName());
        assertNotNull(order.getProduct());
        assertNotNull(order.getStatus());

        assertTrue(id instanceof String);
        assertTrue(order.getCustomerName() instanceof String);
        assertTrue(order.getProduct() instanceof String);
        assertTrue(order.getStatus() instanceof String);
    }

    @Test
    void setters_ShouldUpdateorAddFields() {
        Orders order = new Orders("Kai", "Laptop", "Delivered");
        order.setCustomerName("Thai");
        order.setProduct("Monitor");
        order.setStatus("Not Delivered");
        assertEquals("Thai", order.getCustomerName());
        assertEquals("Monitor", order.getProduct());
        assertEquals("Not Delivered", order.getStatus());
    }

    @Test
    void getters_ShouldReturnFields() {
        Orders order = new Orders("Sai", "Laptop", "CREATED");

        assertEquals("Sai", order.getCustomerName());
        assertEquals("Laptop", order.getProduct());
        assertEquals("CREATED", order.getStatus());
    }
}
