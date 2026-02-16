package org.wita.erp.controllers.product.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.wita.erp.domain.entities.product.dtos.*;

import java.util.UUID;

@Tag(name = "product's category management", description = "Endpoints to list, create, update and delete product's categories on ERP system")
public interface CategoryDocs {

    @Operation(summary = "List Paged Categories", description = "Return a category list with pagination support and name filter. \nRequires CATEGORY_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CATEGORY_READ authority", content = @Content)
    })
    ResponseEntity<Page<CategoryDTO>> getAllCategories(@ParameterObject
                                              Pageable pageable,
                                                       @Parameter(description = "Term used to filter categories by name", example = "Wireless Mouse")
                                              String searchTerm);

    @Operation(summary = "Create a category", description = "Create a new category with a name. Requires CATEGORY_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CATEGORY_CREATE authority", content = @Content),
    })
    ResponseEntity<CategoryDTO> create(CreateCategoryRequestDTO data);

    @Operation(summary = "Update category's data", description = "Update the name of a specific product. \nRequires CATEGORY_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CATEGORY_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    ResponseEntity<CategoryDTO> update(@Parameter(description = "UUID of the category to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                   UUID id,
                                   UpdateCategoryRequestDTO data);

    @Operation(summary = "Remove category", description = "Inactivate a specific category from the system. \nRequires PRODUCT_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CATEGORY_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    ResponseEntity<CategoryDTO> delete(@Parameter(description = "UUID of the category to remove", example = "123e4567-e89b-12d3-a456-426614174000")
                                   UUID id);

}


