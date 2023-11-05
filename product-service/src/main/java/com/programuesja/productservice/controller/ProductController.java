package com.programuesja.productservice.controller;

import com.programuesja.productservice.dto.ProductRequest;
import com.programuesja.productservice.dto.ProductResponse;
import com.programuesja.productservice.model.mapper.ProductMapper;
import com.programuesja.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    /** Mapper class for product */
    ProductMapper productMapper;

    /** Service class for product */
    ProductService productService;

    public ProductController(ProductMapper productMapper, ProductService productService) {
        this.productMapper = productMapper;
        this.productService = productService;
    }

    @PostMapping("/product/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest) {
        productService.createProduct(productMapper.toProduct(productRequest));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts() {
        List<ProductResponse> products = productMapper.toResponseList(productService.getAllProducts());
        return products.isEmpty() ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find products") :
                ResponseEntity.status(HttpStatus.OK).body(products);
    }
}
