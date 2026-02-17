package org.wita.erp.domain.entities.product.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.product.dtos.ProductDTO;
import org.wita.erp.domain.entities.product.dtos.UpdateProductRequestDTO;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.supplier.mappers.SupplierMapper;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, SupplierMapper.class})
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDTO(UpdateProductRequestDTO dto, @MappingTarget Product product);

    ProductDTO toDTO(Product product);
}
