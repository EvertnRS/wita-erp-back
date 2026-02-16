package org.wita.erp.domain.entities.supplier.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.entities.supplier.dtos.SupplierDTO;
import org.wita.erp.domain.entities.supplier.dtos.UpdateSupplierRequestDTO;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSupplierFromDTO(UpdateSupplierRequestDTO dto, @MappingTarget Supplier product);

    SupplierDTO toDTO(Supplier supplier);

}