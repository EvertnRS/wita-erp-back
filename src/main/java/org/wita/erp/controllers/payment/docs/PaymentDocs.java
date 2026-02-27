package org.wita.erp.controllers.payment.docs;

import com.stripe.exception.StripeException;
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
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.PaymentCheckoutDTO;
import org.wita.erp.domain.entities.payment.dtos.PaymentDTO;

import java.util.UUID;

@Tag(name = "payment management", description = "Endpoints to list, create and checkout payment intent sessions.")
public interface PaymentDocs {

    @Operation(summary = "List Paged Payments", description = "Return a payment list with pagination support and name filter. \nRequires PAYMENT_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PAYMENT_READ authority", content = @Content)
    })
    ResponseEntity<Page<PaymentDTO>> getAllPayments(@ParameterObject
                                              Pageable pageable,
                                                    @Parameter(description = "Term used to filter payments by customer name", example = "John Doe")
                                              String searchTerm);

    @Operation(summary = "Create a Payment", description = "Create a new payment intent for a receivable installment. Requires PAYMENT_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PAYMENT_CREATE authority", content = @Content),
    })
    ResponseEntity<PaymentDTO> create(@Parameter(description = "UUID of the receivable for which are trying to create a payment", example = "123e4567-e89b-12d3-a456-426614174000")
                                      UUID receivableId,
                                      CreatePaymentRequestDTO data);

    @Operation(summary = "Create a checkout session", description = "Create a new checkout session for a payment. \nRequires PAYMENT_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout session created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentCheckoutDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PAYMENT_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    ResponseEntity<PaymentCheckoutDTO> checkout(@Parameter(description = "UUID of the payment for which are trying to create a checkout session", example = "123e4567-e89b-12d3-a456-426614174000")
                                               UUID id) throws StripeException;

}


