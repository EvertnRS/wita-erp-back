package org.wita.erp.domain.entities.purchase.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.purchase.Payable;
import org.wita.erp.domain.entities.purchase.dtos.UpdatePayableRequestDTO;

@Mapper(componentModel = "spring")
public interface PayableMapper {
    @Mapping(target = "purchase", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePayableFromDTO(UpdatePayableRequestDTO dto, @MappingTarget Payable payable);

}
