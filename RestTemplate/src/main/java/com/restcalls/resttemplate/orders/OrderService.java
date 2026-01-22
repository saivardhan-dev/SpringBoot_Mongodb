package com.restcalls.resttemplate.orders;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository repo;
    private final OrdersCache cache;
    private final OrdersCacheLoader ordersCacheLoader;

    public OrderService(OrderRepository repo, OrdersCache cache, OrdersCacheLoader ordersCacheLoader) {
        this.repo = repo;
        this.cache = cache;
        this.ordersCacheLoader = ordersCacheLoader;
    }

    @CircuitBreaker(name = "mongoDbCB", fallbackMethod = "getAllFallback")
    public List<Orders> getAll() {
        List<Orders> orders = repo.findAll();
        orders.forEach(cache::put);
        return orders;
    }

    @CircuitBreaker(name = "mongoDbCB", fallbackMethod = "getByIdFallback")
    public Orders getById(String id) {
        Orders order = repo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        cache.put(order);
        return order;
    }

    @CircuitBreaker(name = "mongoDbCB", fallbackMethod = "createFallback")
    public Orders create(Orders order) {
        Orders saved = repo.save(order);
        cache.put(saved);
        System.out.println("Created order with ID: " + saved.getId());
        return saved;
    }

    @CircuitBreaker(name = "mongoDbCB", fallbackMethod = "updateFallback")
    public Orders update(String id, Orders updated) {

        Orders existing = repo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        if (updated.getCustomerName() != null) {
            existing.setCustomerName(updated.getCustomerName());
        }
        if (updated.getProduct() != null) {
            existing.setProduct(updated.getProduct());
        }
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }

        Orders saved = repo.save(existing);
        cache.put(saved);
        return saved;
    }

    @CircuitBreaker(name = "mongoDbCB", fallbackMethod = "deleteFallback")
    public boolean delete(String id) {
        if (!repo.existsById(id))
            throw new OrderNotFoundException("Order not found with id: " + id);

        repo.deleteById(id);
        cache.evict(id);
        return true;
    }

    // -----------------------
    // Fallback methods
    // -----------------------

    public Orders getByIdFallback(String id, Throwable ex) {
        log.warn("Fallback getById({}) - DB unavailable or circuit OPEN. Reason: {}", id, ex.toString());
        Orders cached = cache.get(id);
        if (cached != null) {
            return cached;
        }
        throw new DatabaseUnavailableException("Database unavailable and order not found in cache");
    }

    public List<Orders> getAllFallback(Throwable ex) {
        log.warn("Fallback getAll() - DB unavailable or circuit OPEN. Reason: {}", ex.toString());
        return cache.getAllCached();
    }

    public Orders createFallback(Orders order, Throwable ex) {
        log.warn("Fallback create() - rejecting write. Reason: {}", ex.toString());
        throw new DatabaseUnavailableException("Database unavailable. Cannot create order right now.");
    }

    public Orders updateFallback(String id, Orders updated, Throwable ex) {
        log.warn("Fallback update({}) - rejecting write. Reason: {}", id, ex.toString());
        throw new DatabaseUnavailableException("Database unavailable. Cannot update order right now.");
    }

    public boolean deleteFallback(String id, Throwable ex) {
        log.warn("Fallback delete({}) - rejecting write. Reason: {}", id, ex.toString());
        throw new DatabaseUnavailableException("Database unavailable. Cannot delete order right now.");
    }
}