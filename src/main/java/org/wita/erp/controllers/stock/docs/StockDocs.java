package org.wita.erp.controllers.stock.docs;

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
import org.wita.erp.domain.entities.stock.dtos.*;

import java.util.UUID;

@Tag(name = "Stock movement management", description = "Endpoints to list, create, update and delete stock movement on ERP system")
public interface StockDocs {

    @Operation(summary = "List Paged stock movement", description = "Return a stock movement list with pagination support and name filter. \nRequires STOCK_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock movement retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have STOCK_READ authority", content = @Content)
    })
    ResponseEntity<Page<StockMovementDTO>> getAllStock(@ParameterObject
                                                                  Pageable pageable,
                                                                  @Parameter(description = "Term used to filter stock movement by product's name", example = "Wireless Mouse")
                                                                  String searchTerm);

    @Operation(summary = "Update stock movement data", description = "Update the product, quantity, movement reason, transaction and user of a specific movement stock. \nRequires STOCK_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reason for stock movement updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StockMovementDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have STOCK_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reason for stock movement not found", content = @Content)
    })
    ResponseEntity<StockMovementDTO> update(@Parameter(description = "UUID of the stock movement to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                             UUID id,
                                             UpdateStockRequestDTO data);

    @Operation(summary = "Remove stock movement", description = "Inactivate a specific stock movement from the system. \nRequires STOCK_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock movement deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StockMovementDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have STOCK_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Stock movement not found", content = @Content)
    })
    ResponseEntity<StockMovementDTO> delete(@Parameter(description = "UUID of the stock movement to remove", example = "123e4567-e89b-12d3-a456-426614174000")
                                             UUID id);

}


