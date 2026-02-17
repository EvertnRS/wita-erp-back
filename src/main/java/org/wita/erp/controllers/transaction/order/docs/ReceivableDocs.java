package org.wita.erp.controllers.transaction.order.docs;

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
import org.wita.erp.domain.entities.transaction.order.dtos.DeleteReceivableRequestDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.ReceivableDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.UpdateReceivableRequestDTO;

import java.util.UUID;

@Tag(name = "Receivable management", description = "Endpoints to list, create, update and delete receivables on ERP system")
public interface ReceivableDocs {

    @Operation(summary = "List Paged Receivable", description = "Return a receivable list with pagination support and created date filter. \nRequires RECEIVABLE_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receivable retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have RECEIVABLE_READ authority", content = @Content)
    })
    ResponseEntity<Page<ReceivableDTO>> getAllReceivable(@ParameterObject
                                                Pageable pageable,
                                                         @Parameter(description = "Term used to filter receivables by created date", example = "2024-01-01")
                                                String searchTerm);

    @Operation(summary = "Update receivable data", description = "Update the due date and the payment status for a specific receivable. \nRequires RECEIVABLE_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receivable updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReceivableDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have RECEIVABLE_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Receivable not found", content = @Content)
    })
    ResponseEntity<ReceivableDTO> update(@Parameter(description = "UUID of the receivable to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                    UUID id,
                                    UpdateReceivableRequestDTO data);

    @Operation(summary = "Remove receivable", description = "Inactivate a specific receivable from the system. \nRequires RECEIVABLE_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receivable deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReceivableDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have RECEIVABLE_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Receivable not found", content = @Content)
    })
    ResponseEntity<ReceivableDTO> delete(@Parameter(description = "UUID of the receivable to remove and reason of delete", example = "123e4567-e89b-12d3-a456-426614174000")
                                    UUID id, DeleteReceivableRequestDTO data);

}


