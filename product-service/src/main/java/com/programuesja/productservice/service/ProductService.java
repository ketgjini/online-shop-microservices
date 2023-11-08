package com.programuesja.productservice.service;

import com.programuesja.productservice.model.Product;
import com.programuesja.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(final Product product) {
        productRepository.save(product);
        LOGGER.info("Product {} is saved ", product.getId());
    }

    public List<Product> getAllProducts() {
        LOGGER.info("Retrieving all Products...");
        return productRepository.findAll();
    }
}
