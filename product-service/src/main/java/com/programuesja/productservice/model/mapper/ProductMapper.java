package com.programuesja.productservice.model.mapper;

import com.programuesja.productservice.dto.ProductRequest;
import com.programuesja.productservice.dto.ProductResponse;
import com.programuesja.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public abstract class ProductMapper {
    public abstract ProductResponse toResponse(final Product product);
    public abstract Product toProduct(final ProductRequest productRequest);

    public List<ProductResponse> toResponseList(final List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }

        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
