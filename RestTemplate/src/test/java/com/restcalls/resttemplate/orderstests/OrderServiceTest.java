package com.restcalls.resttemplate.orderstests;

import com.restcalls.resttemplate.orders.OrderNotFoundException;
import com.restcalls.resttemplate.orders.OrderRepository;
import com.restcalls.resttemplate.orders.OrderService;
import com.restcalls.resttemplate.orders.Orders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @Mock
    private OrderRepository repo;

    @InjectMocks
    private OrderService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_shouldReturnListOfOrders() {
        Orders order1 = new Orders("Max", "Watch", "delivered");
        Orders order2 = new Orders("John", "Phone","Out for delivery");

        when(repo.findAll()).thenReturn(List.of(order1, order2));

        List<Orders> result = service.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Max", result.get(0).getCustomerName());
        assertEquals("Phone", result.get(1).getProduct());

        verify(repo, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnOrder() {
        Orders order1 = new Orders("Max", "Watch", "delivered");
        order1.setId("asdflkj");
        Orders order2 = new Orders("John", "Phone","Out for delivery");
        order2.setId("lkjasdf");

        when(repo.findById(order1.getId())).thenReturn(Optional.of(order1));
        when(repo.findById(order2.getId())).thenReturn(Optional.of(order2));

        Orders result1 = service.getById(order1.getId());
        Orders result2 = service.getById(order2.getId());

        assertNotNull(result1);
        assertNotNull(result2);

        assertEquals(order1, result1);
        assertEquals(order2, result2);

        verify(repo, times(1)).findById(order1.getId());
        verify(repo, times(1)).findById(order2.getId());
    }

    @Test
    void create_shouldCreateOrder() {
        Orders order = new Orders("Max", "Watch", "delivered");

        when(repo.save(order)).thenReturn(order);

        Orders result = service.create(order);
        assertNotNull(result);
        assertEquals(order, result);
        verify(repo, times(1)).save(order);
    }

    @Test
    void update_shouldUpdateOrder() {
        Orders order = new Orders("Max", "Watch", "delivered");
        order.setId("asdflkj");
        when(repo.findById(order.getId())).thenReturn(Optional.of(order));
        when(repo.save(order)).thenReturn(order);
    }

    @Test
    void update_shouldThrowException_whenOrderNotFound() {
        Orders order = new Orders("Max", "Watch", "delivered");
        when(repo.findById(order.getId())).thenReturn(Optional.empty());
    }

    @Test
    void delete_shouldDeleteOrder() {
        when(repo.existsById(anyString())).thenReturn(true);
        doNothing().when(repo).deleteById(anyString());
        boolean result = service.delete(anyString());
        assertTrue(result);
        verify(repo, times(1)).deleteById(anyString());
    }

    @Test
    void deleteAll_shouldDeleteOrders() {
        String id = "asdflkj";
        when(repo.existsById(id)).thenReturn(false);
        assertThrows(OrderNotFoundException.class, () -> service.delete(id));
        verify(repo, times(1)).existsById(id);
        verify(repo, never()).deleteById(anyString());
    }
}
