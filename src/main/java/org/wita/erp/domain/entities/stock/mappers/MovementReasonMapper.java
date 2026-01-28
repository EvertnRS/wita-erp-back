package org.wita.erp.domain.entities.stock.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.dtos.UpdateMovementReasonRequestDTO;

@Mapper(componentModel = "spring")
public interface MovementReasonMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMovementReasonFromMovementReason(UpdateMovementReasonRequestDTO dto, @MappingTarget MovementReason movementReason);
}
