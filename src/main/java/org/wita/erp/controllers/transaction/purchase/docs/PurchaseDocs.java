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
import org.wita.erp.domain.entities.transaction.dtos.PurchaseDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.CreateExpensePurchaseRequestDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.CreateReplacementPurchaseRequestDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.ProductInPurchaseDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.UpdatePurchaseRequestDTO;

import java.util.UUID;

@Tag(name = "Purchase management", description = "Endpoints to list, create, update and delete purchases on ERP system")
public interface PurchaseDocs {

    @Operation(summary = "List Paged Purchase", description = "Return a purchase list with pagination support and created date filter. \nRequires PURCHASE_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PURCHASE_READ authority", content = @Content)
    })
    ResponseEntity<Page<PurchaseDTO>> getAllPurchases(@ParameterObject
                                                Pageable pageable,
                                                @Parameter(description = "Term used to filter purchases by created date", example = "John Doe")
                                                String searchTerm);

    @Operation(summary = "Create a replacement purchase", description = "Create a new replacement purchase with transaction code, number of installments, description, buyer, supplier, payment type and products. Requires PURCHASE_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Replacement purchase created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PURCHASE_CREATE authority", content = @Content),
    })
    ResponseEntity<PurchaseDTO> createReplacementPurchase(CreateReplacementPurchaseRequestDTO data);

    @Operation(summary = "Create a expense purchase", description = "Create a new expense purchase with value, transaction code, number of installments, description, buyer, supplier and payment type. Requires PURCHASE_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense purchase created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PURCHASE_CREATE authority", content = @Content),
    })
    ResponseEntity<PurchaseDTO> createExpensePurchase(CreateExpensePurchaseRequestDTO data);

    @Operation(summary = "Add product to a purchase", description = "Add a new product of a specific purchase. \nRequires PURCHASE_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added to purchase successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PURCHASE_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Purchase not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    ResponseEntity<PurchaseDTO> addProductInPurchase(@Parameter(description = "UUID of the purchase to add a product", example = "123e4567-e89b-12d3-a456-426614174000")
                                               UUID purchaseId,
                                               ProductInPurchaseDTO data);

    @Operation(summary = "Remove product to a purchase", description = "Remove a product of a specific purchase. \nRequires PURCHASE_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product removed to purchase successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PURCHASE_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Purchase not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    ResponseEntity<PurchaseDTO> removeProductInPurchase(@Parameter(description = "UUID of the purchase to remove a product", example = "123e4567-e89b-12d3-a456-426614174000")
                                                  UUID purchaseId,
                                                  ProductInPurchaseDTO data);

    @Operation(summary = "Update a replacement purchase data", description = "Update the buyer, supplier, payment type, movement reason, transaction code, value, number of installments and description for a specific replacement purchase. \nRequires PURCHASE_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Replacement purchase updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PURCHASE_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Purchase not found", content = @Content)
    })
    ResponseEntity<PurchaseDTO> update(@Parameter(description = "UUID of the replacement purchase to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                    UUID id,
                                    UpdatePurchaseRequestDTO data);

    @Operation(summary = "Update a expense purchase data", description = "Update the buyer, supplier, payment type, movement reason, transaction code, value, number of installments and description for a specific expense purchase. \nRequires PURCHASE_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense purchase updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PURCHASE_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Purchase not found", content = @Content)
    })
    ResponseEntity<PurchaseDTO> updateExpense(@Parameter(description = "UUID of the expense purchase to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                    UUID id,
                                    UpdatePurchaseRequestDTO data);

    @Operation(summary = "Remove purchase", description = "Inactivate a specific purchase from the system. \nRequires PURCHASE_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PURCHASE_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Purchase not found", content = @Content)
    })
    ResponseEntity<PurchaseDTO> delete(@Parameter(description = "UUID of the purchase to remove", example = "123e4567-e89b-12d3-a456-426614174000")
                                    UUID id);

}


