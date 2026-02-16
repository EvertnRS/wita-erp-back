package org.wita.erp.controllers.transaction.docs;

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
import org.wita.erp.domain.entities.stock.dtos.StockMovementDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateStockRequestDTO;
import org.wita.erp.domain.entities.transaction.Transaction;
import org.wita.erp.domain.entities.transaction.TransactionType;
import org.wita.erp.domain.entities.transaction.dtos.TransactionDTO;

import java.util.UUID;

@Tag(name = "Transaction management", description = "Endpoints to list, create, update and delete transactions on ERP system")
public interface TransactionDocs {

    @Operation(summary = "List Paged transactions", description = "Return a transactions list with pagination support and name filter. \nRequires ORDER_READ and PURCHASE_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ORDER_READ and PURCHASE_READ authority", content = @Content)
    })
    ResponseEntity<Page<TransactionDTO>> getAllTransactions(@ParameterObject
                                                                  Pageable pageable,
                                                            @Parameter(description = "Type of transaction to list", example = "ORDER")
                                                            TransactionType transactionType);

    @Operation(summary = "Remove transactions", description = "Inactivate a specific transaction from the system. \nRequires ORDER_DELETE and PURCHASE_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Transaction.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ORDER_DELETE and PURCHASE_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    })
    ResponseEntity<TransactionDTO> delete(@Parameter(description = "UUID of the transaction to remove", example = "123e4567-e89b-12d3-a456-426614174000")
                                             UUID id);

}


