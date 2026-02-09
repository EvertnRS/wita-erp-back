package org.wita.erp.domain.entities.transaction.order.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.customer.mappers.CustomerMapper;
import org.wita.erp.domain.entities.payment.mappers.PaymentTypeMapper;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.entities.transaction.order.dtos.ReceivableDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.UpdateReceivableRequestDTO;
import org.wita.erp.domain.entities.transaction.purchase.Payable;
import org.wita.erp.domain.entities.transaction.purchase.dtos.UpdatePayableRequestDTO;
import org.wita.erp.domain.entities.user.mappers.UserMapper;

@Mapper(componentModel = "spring",  uses = {
        PaymentTypeMapper.class
})
public interface ReceivableMapper {
    @Mapping(target = "order", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReceivableFromDTO(UpdateReceivableRequestDTO dto, @MappingTarget Receivable receivable);

    @Mapping(source = "order.id", target = "order")
    @Mapping(source = "order.paymentType", target = "paymentType")
    ReceivableDTO toDTO(Receivable receivable);

}
