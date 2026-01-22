package com.restcalls.resttemplate.orders;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrdersCache {

    private final ConcurrentHashMap<String, Orders> cache = new ConcurrentHashMap<>();

    public void put(Orders order) {
        if (order != null && order.getId() != null) {
            cache.put(order.getId(), order);
        }
    }

    public Orders get(String id) {
        return cache.get(id);
    }

    public void evict(String id) {
        cache.remove(id);
    }

    public List<Orders> getAllCached() {
        return new ArrayList<>(cache.values()); // snapshot
    }
}

