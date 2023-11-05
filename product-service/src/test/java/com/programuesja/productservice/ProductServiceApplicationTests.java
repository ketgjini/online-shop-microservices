package com.programuesja.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.programuesja.productservice.dto.ProductRequest;
import com.programuesja.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry properties) {
		properties.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productAsString = mapper.writeValueAsString(productRequest);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/products/product/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productAsString))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	@Test
	void shouldGetAllProducts() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/all")
								.accept(MediaType.APPLICATION_JSON))
								.andExpect(MockMvcResultMatchers.status().isOk())
								.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
								.andReturn();

		String responseJson = result.getResponse().getContentAsString();
		String firstProductName = JsonPath.read(responseJson, "$[0].name");

		Assertions.assertEquals("Asus ROG", firstProductName);
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("Asus ROG")
				.description("Good gaming laptop")
				.price(BigDecimal.valueOf(1500))
				.build();
	}

}
