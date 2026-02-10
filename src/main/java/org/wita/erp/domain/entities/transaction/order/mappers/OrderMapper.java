package org.wita.erp.domain.entities.transaction.order.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.customer.mappers.CustomerMapper;
import org.wita.erp.domain.entities.transaction.order.Order;
import org.wita.erp.domain.entities.transaction.order.OrderItem;
import org.wita.erp.domain.entities.transaction.order.dtos.OrderDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.OrderItemDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.UpdateOrderRequestDTO;
import org.wita.erp.domain.entities.payment.mappers.PaymentTypeMapper;
import org.wita.erp.domain.entities.user.mappers.UserMapper;

@Mapper(componentModel = "spring", uses = {
        CustomerMapper.class,
        UserMapper.class,
        PaymentTypeMapper.class
})
public interface OrderMapper {
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "paymentType", ignore = true)
    @Mapping(target = "transactionCode", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "value", ignore = true)
    @Mapping(target = "discount", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrderFromDTO(UpdateOrderRequestDTO dto, @MappingTarget Order purchase);

    @Mapping(source = "value", target = "total")
    OrderDTO toDTO(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "ProductName")
    OrderItemDTO toItemDTO(OrderItem item);

}
