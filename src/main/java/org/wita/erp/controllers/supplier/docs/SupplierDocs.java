package org.wita.erp.controllers.supplier.docs;

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
import org.wita.erp.domain.entities.product.dtos.CreateProductRequestDTO;
import org.wita.erp.domain.entities.product.dtos.UpdateProductRequestDTO;
import org.wita.erp.domain.entities.supplier.dtos.CreateSupplierRequestDTO;
import org.wita.erp.domain.entities.supplier.dtos.SupplierDTO;
import org.wita.erp.domain.entities.supplier.dtos.UpdateSupplierRequestDTO;

import java.util.UUID;

@Tag(name = "supplier management", description = "Endpoints to list, create, update and delete suppliers on ERP system")
public interface SupplierDocs {

    @Operation(summary = "List Paged Suppliers", description = "Return a supplier list with pagination support and name filter. \nRequires SUPPLIER_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have SUPPLIER_READ authority", content = @Content)
    })
    ResponseEntity<Page<SupplierDTO>> getAllSuppliers(@ParameterObject
                                              Pageable pageable,
                                                      @Parameter(description = "Term used to filter supplier by name", example = "Acme Corporation")
                                              String searchTerm);

    @Operation(summary = "Create a supplier", description = "Create a new supplier with a name, email, address, and document number. Requires SUPPLIER_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have SUPPLIER_CREATE authority", content = @Content),
    })
    ResponseEntity<SupplierDTO> create(CreateSupplierRequestDTO data);

    @Operation(summary = "Update supplier's data", description = "Update the name, email, address, and document number of a specific supplier. \nRequires SUPPLIER_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have SUPPLIER_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content)
    })
    ResponseEntity<SupplierDTO> update(@Parameter(description = "UUID of the supplier to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                   UUID id,
                                   UpdateSupplierRequestDTO data);

    @Operation(summary = "Remove supplier", description = "Inactivate a specific supplier from the system. \nRequires SUPPLIER_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have SUPPLIER_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content)
    })
    ResponseEntity<SupplierDTO> delete(@Parameter(description = "UUID of the supplier to remove", example = "123e4567-e89b-12d3-a456-426614174000")
                                   UUID id);

}


