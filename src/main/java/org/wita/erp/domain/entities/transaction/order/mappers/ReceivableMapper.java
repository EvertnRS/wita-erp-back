package org.wita.erp.domain.entities.transaction.order.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.payment.customer.mappers.CustomerPaymentTypeMapper;
import org.wita.erp.domain.entities.transaction.dtos.ReceivableDTO;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.entities.transaction.order.dtos.UpdateReceivableRequestDTO;

@Mapper(componentModel = "spring",  uses = {
        CustomerPaymentTypeMapper.class
})
public interface ReceivableMapper {
    @Mapping(target = "order", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReceivableFromDTO(UpdateReceivableRequestDTO dto, @MappingTarget Receivable receivable);

    @Mapping(source = "order.id", target = "order")
    @Mapping(source = "order.customerPaymentType", target = "customerPaymentType")
    ReceivableDTO toDTO(Receivable receivable);

}
