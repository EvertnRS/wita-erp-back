package org.wita.erp.domain.entities.stock.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.stock.StockMovement;
import org.wita.erp.domain.entities.stock.dtos.StockMovementDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateStockRequestDTO;

@Mapper(componentModel = "spring")
public interface StockMapper {
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "movementReason", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStockFromDTO(UpdateStockRequestDTO dto, @MappingTarget StockMovement stock);

    @Mapping(target = "product", source = ".")
    @Mapping(target = "movementInfo", source = ".")
    @Mapping(target = "transactionCode", source = "transaction.transactionCode")
    StockMovementDTO StockMovementToDTO(StockMovement entity);

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "quantityInStock", source = "product.quantityInStock")
    StockMovementDTO.Product mapProduct(StockMovement entity);

    @Mapping(target = "movementType", source = "stockMovementType")
    @Mapping(target = "movementReason", source = "movementReason.reason")
    StockMovementDTO.MovementInfo mapMovementInfo(StockMovement entity);
}
