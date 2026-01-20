package com.restcalls.resttemplate.orders;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    public List<Orders> getAll() {
        return repo.findAll();
    }

    public Orders getById(String id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with id: " + id)
                );
    }

    public Orders create(Orders order) {
        return repo.save(order);
    }

    public Orders update(String id, Orders updated) {

        Orders existing = repo.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with id: " + id)
                );

        if (updated.getCustomerName() != null) {
            existing.setCustomerName(updated.getCustomerName());
        }
        if (updated.getProduct() != null) {
            existing.setProduct(updated.getProduct());
        }
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }

        return repo.save(existing);
    }

    public boolean delete(String id) {
        if (!repo.existsById(id)) throw new OrderNotFoundException("Order not found with id: " + id);
        repo.deleteById(id);
        return true;
    }
}
