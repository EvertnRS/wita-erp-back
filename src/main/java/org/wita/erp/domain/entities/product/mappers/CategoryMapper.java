package org.wita.erp.domain.entities.product.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.product.Category;
import org.wita.erp.domain.entities.product.dtos.CategoryDTO;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDTO(Category category);
}
