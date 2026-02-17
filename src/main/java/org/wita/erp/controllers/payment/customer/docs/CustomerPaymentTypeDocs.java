package org.wita.erp.controllers.payment.customer.docs;

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
import org.wita.erp.domain.entities.payment.customer.dto.CreateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.dto.CustomerPaymentTypeDTO;
import org.wita.erp.domain.entities.payment.customer.dto.DeleteCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.dto.UpdateCustomerPaymentTypeRequestDTO;

import java.util.UUID;

@Tag(name = "customer's payment type management", description = "Endpoints to list, create, update and delete customer's on ERP system")
public interface CustomerPaymentTypeDocs {

    @Operation(summary = "List Paged Customer's payment types", description = "Return a customer's payment type list with pagination support and name filter. \nRequires CUSTOMER_PAYMENT_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer's payment type retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CUSTOMER_PAYMENT_READ authority", content = @Content)
    })
    ResponseEntity<Page<CustomerPaymentTypeDTO>> getAllCustomerPaymentTypes(@ParameterObject
                                                                            Pageable pageable,
                                                                            @Parameter(description = "Term used to filter customers by customer's name", example = "John Doe")
                                                                            String searchTerm);

    @Operation(summary = "Create a customer's payment type", description = "Create a new customer's payment type with indications if the payment type is immediate, allows installments, allows refund and the customer's preferred payment type. Requires CUSTOMER_PAYMENT_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer's payment type created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerPaymentTypeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CUSTOMER_PAYMENT_CREATE authority", content = @Content),
    })
    ResponseEntity<CustomerPaymentTypeDTO> create(CreateCustomerPaymentTypeRequestDTO data);

    @Operation(summary = "Update customer's payment type data", description = "Update the indications of the payment type, if is immediate, allows installments, and if allows refund of a specific customer's payment type. \nRequires CUSTOMER_PAYMENT_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer's payment type updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerPaymentTypeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CUSTOMER_PAYMENT_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer's payment type not found", content = @Content)
    })
    ResponseEntity<CustomerPaymentTypeDTO> update(@Parameter(description = "UUID of the customer's payment type to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                       UUID id,
                                       UpdateCustomerPaymentTypeRequestDTO data);

    @Operation(summary = "Remove customer's payment type", description = "Inactivate a specific customer's payment type from the system. \nRequires CUSTOMER_PAYMENT_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer's payment type deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerPaymentTypeDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CUSTOMER_PAYMENT_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer's payment type not found", content = @Content)
    })
    ResponseEntity<CustomerPaymentTypeDTO> delete(@Parameter(description = "UUID of the customer's payment type to remove and reason of delete", example = "123e4567-e89b-12d3-a456-426614174000")
                                       UUID id, DeleteCustomerPaymentTypeRequestDTO data);

}


