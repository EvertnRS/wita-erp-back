package org.wita.erp.domain.product.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.product.dtos.UpdateProductRequestDTO;
import org.wita.erp.domain.product.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromProduct(UpdateProductRequestDTO dto, @MappingTarget Product product);

}
