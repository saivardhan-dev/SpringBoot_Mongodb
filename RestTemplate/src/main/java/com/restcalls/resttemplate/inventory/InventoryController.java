package com.restcalls.resttemplate.inventory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<Inventory> getAllInventory() { return service.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventory(@PathVariable String id) {
        Inventory inventory = service.getById(id);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        Inventory saved = service.create(inventory);
        return ResponseEntity
                .created(URI.create("/inventory/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable String id,
                                                     @RequestBody Inventory updated) {
        Inventory saved = service.update(id, updated);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInventory(@PathVariable String id) {
        boolean deleted = service.delete(id);
        return deleted ? ResponseEntity.ok("Inventory deleted successfully")
                : ResponseEntity.status(404).body("Inventory not found");
    }
}
