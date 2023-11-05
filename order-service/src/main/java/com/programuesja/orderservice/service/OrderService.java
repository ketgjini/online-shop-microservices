package com.programuesja.orderservice.service;

import com.programuesja.orderservice.dto.InventoryResponse;
import com.programuesja.orderservice.model.Order;
import com.programuesja.orderservice.model.OrderItems;
import com.programuesja.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    public static final String INVENTORY_URL = "http://localhost:8082/api/inventory";

    private OrderRepository orderRepository;

    private WebClient webClient;

    public OrderService(OrderRepository orderRepository, WebClient webClient) {
        this.orderRepository = orderRepository;
        this.webClient = webClient;
    }

    public void placeOrder(List<OrderItems> orderItemsList) {
        final Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderItemsList(orderItemsList);

        List<String> skuCodes = order.getOrderItemsList().stream().map(OrderItems::getSkuCode).toList();

        InventoryResponse[] inventoryResponseArray = webClient.get()
                                     .uri(INVENTORY_URL,
                                             uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                                     .retrieve()
                                     .bodyToMono(InventoryResponse[].class)
                                     .block();

        boolean allProductsInStock = false;
        if(inventoryResponseArray.length > 0) {
            allProductsInStock =  Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        }

        if(allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock. Try again later!");
        }
    }
}
