package org.wita.erp.domain.Product.Mappers;

import org.mapstruct.*;
import org.wita.erp.domain.Product.Dtos.UpdateProductRequestDTO;
import org.wita.erp.domain.Product.Product;
import org.wita.erp.domain.User.Dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.User.Dtos.UserDTO;
import org.wita.erp.domain.User.User;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromProduct(UpdateProductRequestDTO dto, @MappingTarget Product product);

}
