package com.programuesja.orderservice.controller;

import com.programuesja.orderservice.dto.OrderRequest;
import com.programuesja.orderservice.model.mapper.OrderItemMapper;
import com.programuesja.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    private final OrderItemMapper mapper;

    public OrderController(final OrderService orderService, final OrderItemMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @PostMapping("/order/create")
    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryFallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<ResponseEntity<?>> placeOrder(@RequestBody final OrderRequest orderRequest) {
        orderService.placeOrder(mapper.toOrderItems(orderRequest.getOrderItemsDTOList()));
        return CompletableFuture.supplyAsync(() -> ResponseEntity.status(HttpStatus.CREATED)
                .body("Order placed successfully"));
    }

    /** Fallback method when circuit breaker STATUS is not CLOSED */
    public CompletableFuture<ResponseEntity<?>> inventoryFallbackMethod(final OrderRequest orderRequest, final Throwable t) {
        LOGGER.warn("Fallback method invoked due to circuit open.");
        return CompletableFuture.supplyAsync(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Oops! Something went wrong with your order. Please try again in a bit."));
    }
}
