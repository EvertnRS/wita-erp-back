package org.wita.erp.domain.entities.payment.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.payment.PaymentMethod;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.dtos.PaymentTypeDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.entities.user.dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;

@Mapper(componentModel = "spring")
public interface PaymentTypeMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePaymentTypeFromDTO(UpdatePaymentTypeRequestDTO dto, @MappingTarget PaymentType paymentType);

    PaymentTypeDTO toPaymentTypeDTO(PaymentType paymentType);
}
