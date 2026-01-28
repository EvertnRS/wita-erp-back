package org.wita.erp.domain.entities.stock.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.stock.StockMovement;
import org.wita.erp.domain.entities.stock.dtos.UpdateStockRequestDTO;

@Mapper(componentModel = "spring")
public interface StockMapper {
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "movementReason", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStockFromStock(UpdateStockRequestDTO dto, @MappingTarget StockMovement stock);
}
