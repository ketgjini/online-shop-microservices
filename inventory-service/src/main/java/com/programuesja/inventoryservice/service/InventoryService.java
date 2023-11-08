package com.programuesja.inventoryservice.service;

import com.programuesja.inventoryservice.dto.InventoryResponse;
import com.programuesja.inventoryservice.model.Inventory;
import com.programuesja.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    private InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(final List<String> skuCodes) {
        LOGGER.info("Checking inventory...");
        List<Inventory> inventories = inventoryRepository.findBySkuCodeIn(skuCodes);
        return inventories.stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
    }
}
