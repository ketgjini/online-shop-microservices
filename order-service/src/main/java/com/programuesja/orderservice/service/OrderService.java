package com.programuesja.orderservice.service;

import com.programuesja.orderservice.dto.InventoryResponse;
import com.programuesja.orderservice.event.OrderPlacedEvent;
import com.programuesja.orderservice.model.Order;
import com.programuesja.orderservice.model.OrderItems;
import com.programuesja.orderservice.repository.OrderRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    public static final String INVENTORY_URL = "http://inventory-service/api/inventory";

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    private final ObservationRegistry observationRegistry;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder,
                        ObservationRegistry observationRegistry, KafkaTemplate kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
        this.observationRegistry = observationRegistry;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void placeOrder(final List<OrderItems> orderItemsList) {
        final Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderItemsList(orderItemsList);

        final List<String> skuCodes = order.getOrderItemsList().stream().map(OrderItems::getSkuCode).toList();

        final Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup",
                this.observationRegistry);

        inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");
        inventoryServiceObservation.observe(() -> {
            final List<InventoryResponse> inventoryResponses = fetchInventoryResponses(skuCodes);

            boolean allProductsInStock = false;
            if(!inventoryResponses.isEmpty()) {
                allProductsInStock = inventoryResponses.stream().allMatch(InventoryResponse::isInStock);
            }

            if(allProductsInStock) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            } else {
                throw new IllegalArgumentException("Product is not in stock. Try again later!");
            }
        });
    }

    private List<InventoryResponse> fetchInventoryResponses(final List<String> skuCodes) {
        final WebClient webClient = webClientBuilder.build();

        return webClient.get()
                .uri(INVENTORY_URL, uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block();
    }
}
