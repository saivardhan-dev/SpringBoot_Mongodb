package com.restcalls.resttemplate;

import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<Orders> getAllOrders() {return service.getAll();}

    @GetMapping("/{id}")
    public ResponseEntity<Orders> getOrder(@PathVariable String id) {
        Orders order = service.getById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<Orders> createOrder(@RequestBody Orders order) {
        Orders saved = service.create(order);
        return ResponseEntity
                .created(URI.create("/orders/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Orders> updateOrder(@PathVariable String id,
                                              @RequestBody Orders updated) {
        Orders saved = service.update(id, updated);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {
        boolean deleted = service.delete(id);
        return deleted ? ResponseEntity.ok("Order deleted successfully")
                : ResponseEntity.status(404).body("Order not found");
    }
}

