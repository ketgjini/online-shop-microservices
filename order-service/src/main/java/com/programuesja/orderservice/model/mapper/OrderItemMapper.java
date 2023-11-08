package com.programuesja.orderservice.model.mapper;

import com.programuesja.orderservice.dto.OrderItemsDTO;
import com.programuesja.orderservice.model.OrderItems;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public abstract class OrderItemMapper {
    public abstract List<OrderItems> toOrderItems(final List<OrderItemsDTO> orderItemsDTOList);

    OrderItems toOrderItems(final OrderItemsDTO orderItemsDTO) {
        if (orderItemsDTO == null) {
            return null;
        }

        OrderItems orderItems = new OrderItems();
        orderItems.setId(orderItemsDTO.getId());
        orderItems.setSkuCode(orderItemsDTO.getSkuCode());
        orderItems.setPrice(orderItemsDTO.getPrice());
        orderItems.setQuantity(orderItemsDTO.getQuantity());
        return orderItems;
    }
}
