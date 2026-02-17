package org.wita.erp.domain.entities.stock.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.StockMovement;
import org.wita.erp.domain.entities.stock.dtos.MovementReasonDTO;
import org.wita.erp.domain.entities.stock.dtos.StockMovementDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateStockRequestDTO;

@Mapper(componentModel = "spring")
public interface MovementReasonMapper {
    MovementReasonDTO toDTO(MovementReason entity);

}
