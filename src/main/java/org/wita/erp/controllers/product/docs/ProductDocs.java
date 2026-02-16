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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.wita.erp.domain.entities.product.dtos.CreateProductRequestDTO;
import org.wita.erp.domain.entities.product.dtos.ProductDTO;
import org.wita.erp.domain.entities.product.dtos.UpdateProductRequestDTO;

import java.util.UUID;

@Tag(name = "product management", description = "Endpoints to list, create, update and delete products on ERP system")
public interface ProductDocs {

    @Operation(summary = "List Paged Products", description = "Return a product list with pagination support and name filter. \nRequires PRODUCT_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PRODUCT_READ authority", content = @Content)
    })
    ResponseEntity<Page<ProductDTO>> getAllProducts(@ParameterObject
                                              Pageable pageable,
                                                    @Parameter(description = "Term used to filter products by name", example = "Wireless Mouse")
                                              String searchTerm);

    @Operation(summary = "Create a product", description = "Create a new product with a name, price, discount, quantity, category and the supplier of the product. Requires PRODUCT_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PRODUCT_CREATE authority", content = @Content),
    })
    ResponseEntity<ProductDTO> create(CreateProductRequestDTO data);

    @Operation(summary = "Update product's data", description = "Update the name, price, quantity, category or supplier of a specific product. \nRequires PRODUCT_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PRODUCT_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    ResponseEntity<ProductDTO> update(@Parameter(description = "UUID of the product to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                   UUID id,
                                   UpdateProductRequestDTO data);

    @Operation(summary = "Remove product", description = "Inactivate a specific product from the system. \nRequires PRODUCT_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PRODUCT_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    ResponseEntity<ProductDTO> delete(@Parameter(description = "UUID of the product to remove", example = "123e4567-e89b-12d3-a456-426614174000")
                                   UUID id);

}


