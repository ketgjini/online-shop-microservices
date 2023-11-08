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

    public static final String INVENTORY_URL = "http://inventory-service/api/inventory";

    private OrderRepository orderRepository;

    private WebClient.Builder webClientBuilder;

    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public void placeOrder(List<OrderItems> orderItemsList) {
        final Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderItemsList(orderItemsList);

        List<String> skuCodes = order.getOrderItemsList().stream().map(OrderItems::getSkuCode).toList();

        List<InventoryResponse> inventoryResponses = fetchInventoryResponses(skuCodes);

        boolean allProductsInStock = false;
        if(!inventoryResponses.isEmpty()) {
            allProductsInStock = inventoryResponses.stream().allMatch(InventoryResponse::isInStock);
        }

        if(allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock. Try again later!");
        }
    }

    private List<InventoryResponse> fetchInventoryResponses(List<String> skuCodes) {
        WebClient webClient = webClientBuilder.build();

        return webClient.get()
                .uri(INVENTORY_URL, uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block();
    }
}
