package org.wita.erp.domain.entities.purchase.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.order.Order;
import org.wita.erp.domain.entities.order.dtos.OrderDTO;
import org.wita.erp.domain.entities.payment.mappers.PaymentTypeMapper;
import org.wita.erp.domain.entities.purchase.Purchase;
import org.wita.erp.domain.entities.purchase.dtos.PurchaseDTO;
import org.wita.erp.domain.entities.purchase.dtos.UpdatePurchaseRequestDTO;
import org.wita.erp.domain.entities.supplier.mappers.SupplierMapper;
import org.wita.erp.domain.entities.user.mappers.UserMapper;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class,
        SupplierMapper.class,
        PaymentTypeMapper.class
})
public interface PurchaseMapper {
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "paymentType", ignore = true)
    @Mapping(target = "transactionCode", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePurchaseFromDTO(UpdatePurchaseRequestDTO dto, @MappingTarget Purchase purchase);

    @Mapping(source = "value", target = "total")
    PurchaseDTO toDTO(Purchase purchase);

}
