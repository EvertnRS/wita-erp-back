package org.wita.erp.domain.entities.order.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.customer.dtos.CustomerDTO;
import org.wita.erp.domain.entities.customer.mappers.CustomerMapper;
import org.wita.erp.domain.entities.order.Order;
import org.wita.erp.domain.entities.order.OrderItem;
import org.wita.erp.domain.entities.order.dtos.OrderDTO;
import org.wita.erp.domain.entities.order.dtos.OrderItemDTO;
import org.wita.erp.domain.entities.order.dtos.UpdateOrderRequestDTO;
import org.wita.erp.domain.entities.payment.PaymentMethod;
import org.wita.erp.domain.entities.payment.dtos.PaymentTypeDTO;
import org.wita.erp.domain.entities.payment.mappers.PaymentTypeMapper;
import org.wita.erp.domain.entities.purchase.Purchase;
import org.wita.erp.domain.entities.purchase.dtos.UpdatePurchaseRequestDTO;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.entities.user.dtos.SellerDTO;
import org.wita.erp.domain.entities.user.mappers.UserMapper;

@Mapper(componentModel = "spring", uses = {
        CustomerMapper.class,
        UserMapper.class,
        PaymentTypeMapper.class
})
public interface OrderMapper {
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "paymentType", ignore = true)
    @Mapping(target = "transactionCode", ignore = true)
    @Mapping(target = "items", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrderFromDTO(UpdateOrderRequestDTO dto, @MappingTarget Order purchase);

    @Mapping(source = "value", target = "total")
    OrderDTO toDTO(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "ProductName")
    OrderItemDTO toItemDTO(OrderItem item);

}
