package org.wita.erp.controllers.customer.docs;

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
import org.wita.erp.domain.entities.customer.dtos.CreateCustomerRequestDTO;
import org.wita.erp.domain.entities.customer.dtos.CustomerDTO;
import org.wita.erp.domain.entities.customer.dtos.DeleteCustomerRequestDTO;
import org.wita.erp.domain.entities.customer.dtos.UpdateCustomerRequestDTO;

import java.util.UUID;

@Tag(name = "customer management", description = "Endpoints to list, create, update and delete customer's on ERP system")
public interface CustomerDocs {

    @Operation(summary = "List Paged Customers", description = "Return a customer list with pagination support and name filter. \nRequires CUSTOMER_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CUSTOMER_READ authority", content = @Content)
    })
    ResponseEntity<Page<CustomerDTO>> getAllCustomers(@ParameterObject
                                              Pageable pageable,
                                                      @Parameter(description = "Term used to filter customers by name", example = "John Doe")
                                              String searchTerm);

    @Operation(summary = "Create a customer", description = "Create a new customer with a name, email, address, document number and birth date. Requires CUSTOMER_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CUSTOMER_CREATE authority", content = @Content),
    })
    ResponseEntity<CustomerDTO> create(CreateCustomerRequestDTO data);

    @Operation(summary = "Update customer's data", description = "Update the name, email, address, document number and birth date of a specific customer. \nRequires CUSTOMER_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CUSTOMER_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    ResponseEntity<CustomerDTO> update(@Parameter(description = "UUID of the customer to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                   UUID id,
                                   UpdateCustomerRequestDTO data);

    @Operation(summary = "Remove customer", description = "Inactivate a specific customer from the system. \nRequires CUSTOMER_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have CUSTOMER_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    ResponseEntity<CustomerDTO> delete(@Parameter(description = "UUID of the customer to remove and reason of delete", example = "123e4567-e89b-12d3-a456-426614174000")
                                   UUID id, DeleteCustomerRequestDTO data);

}


