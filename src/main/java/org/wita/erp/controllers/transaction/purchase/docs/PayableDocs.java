package org.wita.erp.controllers.transaction.purchase.docs;

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
import org.wita.erp.domain.entities.transaction.purchase.dtos.PayableDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.UpdatePayableRequestDTO;

import java.util.UUID;

@Tag(name = "Payable management", description = "Endpoints to list, create, update and delete payables on ERP system")
public interface PayableDocs {

    @Operation(summary = "List Paged Payable", description = "Return a payable list with pagination support and created date filter. \nRequires PAYABLE_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payable retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PAYABLE_READ authority", content = @Content)
    })
    ResponseEntity<Page<PayableDTO>> getAllPayable(@ParameterObject
                                                Pageable pageable,
                                                   @Parameter(description = "Term used to filter payables by created date", example = "2024-01-01")
                                                String searchTerm);

    @Operation(summary = "Update payable data", description = "Update the due date and the payment status for a specific payable. \nRequires PAYABLE_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payable updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PayableDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PAYABLE_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payable not found", content = @Content)
    })
    ResponseEntity<PayableDTO> update(@Parameter(description = "UUID of the payable to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                    UUID id,
                                    UpdatePayableRequestDTO data);

    @Operation(summary = "Remove payable", description = "Inactivate a specific payable from the system. \nRequires PAYABLE_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payable deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PayableDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PAYABLE_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payable not found", content = @Content)
    })
    ResponseEntity<PayableDTO> delete(@Parameter(description = "UUID of the payable to remove", example = "123e4567-e89b-12d3-a456-426614174000")
                                    UUID id);

}


