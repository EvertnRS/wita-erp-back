package org.wita.erp.domain.entities.product.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.product.dtos.UpdateProductRequestDTO;
import org.wita.erp.domain.entities.product.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromProduct(UpdateProductRequestDTO dto, @MappingTarget Product product);

}
