package com.restcalls.resttemplate.inventory;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository repo;

    public InventoryService(InventoryRepository repo) {
        this.repo = repo;
    }

    public List<Inventory> getAll() {
        return repo.findAll();
    }

    public Inventory getById(String id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new InventoryNotFoundException("Inventory not found with id: " + id)
                );
    }

    public Inventory create(Inventory inventory) {
        return repo.save(inventory);
    }

    public Inventory update(String id, Inventory updated) {
        Inventory existing = repo.findById(id)
                .orElseThrow(() ->
                        new InventoryNotFoundException("Inventory not found with id: " + id)
                );

        if (updated.getProductName() != null) {
            existing.setProductName(updated.getProductName());
        }
        if (updated.getLocation() != null) {
            existing.setLocation(updated.getLocation());
        }
        if (updated.getAvailability() != null) {
            existing.setAvailability(updated.getAvailability());
        }

        return repo.save(existing);
    }

    public boolean delete(String id) {
        if (!repo.existsById(id)) throw new InventoryNotFoundException("Inventory not found with id: " + id);
        repo.deleteById(id);
        return true;
    }
}
