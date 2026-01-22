package com.restcalls.resttemplate.orders;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OrdersCacheLoader implements CommandLineRunner {

    private final OrdersCache cache;

    public OrdersCacheLoader(OrdersCache cache) {
        this.cache = cache;
    }

    @Override
    public void run(String... args) {

        Orders o1 = new Orders("X from Cache", "Laptop", "NOT DELIVERED");
        o1.setId("x1");

        Orders o2 = new Orders("Y from Cache", "Phone", "DELIVERED");
        o2.setId("y1");

        Orders o3 = new Orders("Z from Cache", "Tablet", "NOT DELIVERED");
        o3.setId("z1");

        Orders o4 = new Orders("V from Cache", "Xbox", "DELIVERED");
        o4.setId("v1");

        cache.put(o1);
        cache.put(o2);
        cache.put(o3);
        cache.put(o4);
    }
}