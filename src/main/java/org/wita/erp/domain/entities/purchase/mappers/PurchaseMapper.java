package org.wita.erp.domain.entities.purchase.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.purchase.Purchase;
import org.wita.erp.domain.entities.purchase.dtos.UpdatePurchaseRequestDTO;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "paymentType", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePurchaseFromDTO(UpdatePurchaseRequestDTO dto, @MappingTarget Purchase purchase);

}
