package com.restcalls.resttemplate;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    public List<Orders> getAll() {
        return repo.findAll();
    }

    public Optional<Orders> getById(String id) {
        return repo.findById(id);
    }

    public Orders create(Orders order) {
        return repo.save(order);
    }

    public Optional<Orders> update(String id, Orders updated) {
        return repo.findById(id).map(existing -> {
            if(updated.getCustomerName() != null) {
                existing.setCustomerName(updated.getCustomerName());
            }
            if(updated.getProduct() != null) {
                existing.setProduct(updated.getProduct());
            }
            if(updated.getStatus() != null) {
                existing.setStatus(updated.getStatus());
            }
            return repo.save(existing);
        });
    }

    public boolean delete(String id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
}
