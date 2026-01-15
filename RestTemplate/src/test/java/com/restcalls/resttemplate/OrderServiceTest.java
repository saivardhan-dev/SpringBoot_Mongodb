package com.restcalls.resttemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

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
}
