package com.programuesja.orderservice.controller;

import com.programuesja.orderservice.dto.OrderRequest;
import com.programuesja.orderservice.model.mapper.OrderItemMapper;
import com.programuesja.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private OrderService orderService;

    private OrderItemMapper mapper;

    public OrderController(OrderService orderService, OrderItemMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @PostMapping("/order/create")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest) {
        orderService.placeOrder(mapper.toOrderItems(orderRequest.getOrderItemsDTOList()));
        return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully");
    }
}
