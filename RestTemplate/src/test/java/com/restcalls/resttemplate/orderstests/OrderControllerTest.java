package com.restcalls.resttemplate.orderstests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restcalls.resttemplate.GlobalExceptionHandler;
import com.restcalls.resttemplate.orders.OrderController;
import com.restcalls.resttemplate.orders.OrderNotFoundException;
import com.restcalls.resttemplate.orders.OrderService;
import com.restcalls.resttemplate.orders.Orders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @Mock
    private OrderService service;

    private OrderController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new OrderController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        mapper = new ObjectMapper();
    }

    @Test
    void getAll_shouldReturnListOfOrders() throws Exception {
        Orders o1 = new Orders("Max", "Watch", "delivered");
        o1.setId("id1");
        Orders o2 = new Orders("John", "Phone", "Out for delivery");
        o2.setId("id2");

        when(service.getAll()).thenReturn(List.of(o1, o2));

        mockMvc.perform(get("/orders").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].customerName", is("Max")))
                .andExpect(jsonPath("$[1].product", is("Phone")));
    }

    @Test
    void getById_shouldReturnOrder() throws Exception {
        Orders o = new Orders("Max", "Watch", "delivered");
        o.setId("abc123");

        when(service.getById("abc123")).thenReturn(o);

        mockMvc.perform(get("/orders/{id}", "abc123").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("abc123")))
                .andExpect(jsonPath("$.customerName", is("Max")))
                .andExpect(jsonPath("$.product", is("Watch")));
    }

    @Test
    void create_shouldReturnCreatedWithLocation() throws Exception {
        Orders toCreate = new Orders("Max", "Watch", "delivered");
        Orders saved = new Orders("Max", "Watch", "delivered");
        saved.setId("new-id");

        when(service.create(any(Orders.class))).thenReturn(saved);

        String json = mapper.writeValueAsString(toCreate);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orders/new-id"))
                .andExpect(jsonPath("$.id", is("new-id")))
                .andExpect(jsonPath("$.customerName", is("Max")));
    }

    @Test
    void update_shouldReturnUpdatedOrder() throws Exception {
        Orders updated = new Orders("Max Updated", "Watch", "shipped");
        updated.setId("u1");

        when(service.update(anyString(), any(Orders.class))).thenReturn(updated);

        String json = mapper.writeValueAsString(updated);

        mockMvc.perform(put("/orders/{id}", "u1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("u1")))
                .andExpect(jsonPath("$.customerName", is("Max Updated")));
    }

    @Test
    void delete_shouldReturnOk_whenDeleted() throws Exception {
        when(service.delete("d1")).thenReturn(true);

        mockMvc.perform(delete("/orders/{id}", "d1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted successfully"));
    }

    @Test
    void delete_shouldReturnNotFound_whenServiceThrows() throws Exception {
        doThrow(new OrderNotFoundException("Order not found with id: x")).when(service).delete("x");

        mockMvc.perform(delete("/orders/{id}", "x").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Order not found with id: x")));
    }
}
