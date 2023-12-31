package com.programuesja.inventoryservice.controller;

import com.programuesja.inventoryservice.dto.InventoryResponse;
import com.programuesja.inventoryservice.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(final InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam final List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
}
