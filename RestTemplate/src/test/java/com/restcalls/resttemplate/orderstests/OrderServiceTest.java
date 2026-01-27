package com.restcalls.resttemplate.orderstests;

import com.restcalls.resttemplate.orders.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository repo;

    @Mock
    private OrdersCache cache;

    @Mock
    private OrdersCacheLoader cacheLoader;

    @InjectMocks
    private OrderService service;

    private Orders sample;
    private Orders sampleSaved;

    @BeforeEach
    void setUp() {
        sample = new Orders("Alice", "Book", "NOT DELIVERED");
        sample.setId("1");

        sampleSaved = new Orders("Alice", "Book", "NOT DELIVERED");
        sampleSaved.setId("1");
    }

    @Test
    void createOrder_success_savesAndReturnsOrder() {
        when(repo.save(any(Orders.class))).thenReturn(sampleSaved);

        Orders result = service.create(sample);

        assertNotNull(result);
        assertEquals("1", result.getId());
        verify(repo, times(1)).save(sample);
        verify(cache, times(1)).put(sampleSaved);
    }

    @Test
    void getOrderById_returnsOrder_whenPresent() {
        when(repo.findById("1")).thenReturn(Optional.of(sample));

        Orders result = service.getById("1");

        assertNotNull(result);
        assertEquals("Alice", result.getCustomerName());
        verify(repo, times(1)).findById("1");
        verify(cache, times(1)).put(sample);
    }

    @Test
    void getOrderById_throwsOrderNotFound_whenMissing() {
        when(repo.findById("missing")).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.getById("missing"));
        verify(repo, times(1)).findById("missing");
        verifyNoInteractions(cache);
    }

    @Test
    void getAll_returnsOrders_and_putsInCache() {
        Orders o2 = new Orders("Bob", "Phone", "DELIVERED");
        o2.setId("2");
        List<Orders> list = Arrays.asList(sample, o2);

        when(repo.findAll()).thenReturn(list);

        List<Orders> result = service.getAll();

        assertEquals(2, result.size());
        verify(repo, times(1)).findAll();
        verify(cache, times(1)).put(sample);
        verify(cache, times(1)).put(o2);
    }

    @Test
    void updateOrder_success_updatesAndReturns() {
        Orders existing = new Orders("Old", "OldProd", "NOT DELIVERED");
        existing.setId("1");

        Orders updated = new Orders("NewName", "NewProd", "DELIVERED");

        Orders saved = new Orders("NewName", "NewProd", "DELIVERED");
        saved.setId("1");

        when(repo.findById("1")).thenReturn(Optional.of(existing));
        when(repo.save(any(Orders.class))).thenReturn(saved);

        Orders result = service.update("1", updated);

        assertEquals("NewName", result.getCustomerName());
        assertEquals("NewProd", result.getProduct());
        assertEquals("DELIVERED", result.getStatus());

        verify(repo, times(1)).findById("1");
        verify(repo, times(1)).save(existing);
        verify(cache, times(1)).put(saved);
    }

    @Test
    void deleteOrder_success_deletesAndEvictsCache() {
        when(repo.existsById("1")).thenReturn(true);
        doNothing().when(repo).deleteById("1");

        boolean result = service.delete("1");

        assertTrue(result);
        verify(repo, times(1)).existsById("1");
        verify(repo, times(1)).deleteById("1");
        verify(cache, times(1)).evict("1");
    }

    @Test
    void deleteOrder_throwsOrderNotFound_whenMissing() {
        when(repo.existsById("missing")).thenReturn(false);

        assertThrows(OrderNotFoundException.class, () -> service.delete("missing"));
        verify(repo, times(1)).existsById("missing");
        verify(cache, never()).evict(anyString());
    }
}
