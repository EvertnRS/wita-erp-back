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
import org.wita.erp.domain.entities.stock.dtos.CreateMovementReasonRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.DeleteMovementReasonRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.MovementReasonDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateMovementReasonRequestDTO;

import java.util.UUID;

@Tag(name = "Reason for stock movement management", description = "Endpoints to list, create, update and delete reason for movement stock on ERP system")
public interface MovementReasonDocs {

    @Operation(summary = "List Paged Reason for stock movement", description = "Return a reason for movement stock list with pagination support and reason filter. \nRequires REASON_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reason for stock movement retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have REASON_READ authority", content = @Content)
    })
    ResponseEntity<Page<MovementReasonDTO>> getAllMovementReason(@ParameterObject
                                                                  Pageable pageable,
                                                                  @Parameter(description = "Term used to filter movement reason by name", example = "Stock Adjustment")
                                                                  String searchTerm);

    @Operation(summary = "Create a reason for movement stock", description = "Create a new reason for movement stock. Requires REASON_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reason for stock movement created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovementReasonDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have REASON_CREATE authority", content = @Content),
    })
    ResponseEntity<MovementReasonDTO> create(CreateMovementReasonRequestDTO data);

    @Operation(summary = "Update reason for movement stock data", description = "Update the reason for a specific stock movement reason. \nRequires REASON_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reason for stock movement updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovementReasonDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have REASON_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reason for stock movement not found", content = @Content)
    })
    ResponseEntity<MovementReasonDTO> update(@Parameter(description = "UUID of the reason for movement stock to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                             UUID id,
                                             UpdateMovementReasonRequestDTO data);

    @Operation(summary = "Remove reason for movement stock", description = "Inactivate a specific reason for movement stock from the system. \nRequires REASON_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reason for stock movement deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovementReasonDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have REASON_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reason for stock movement not found", content = @Content)
    })
    ResponseEntity<MovementReasonDTO> delete(@Parameter(description = "UUID of the reason for movement stock to remove and reason of delete", example = "123e4567-e89b-12d3-a456-426614174000")
                                             UUID id, DeleteMovementReasonRequestDTO data);

}


