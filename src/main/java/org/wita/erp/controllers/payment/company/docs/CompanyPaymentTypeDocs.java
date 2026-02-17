package org.wita.erp.controllers.payment.company.docs;

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
import org.wita.erp.domain.entities.payment.company.dtos.CompanyPaymentTypeDTO;
import org.wita.erp.domain.entities.payment.company.dtos.CreateCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.company.dtos.DeleteCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.company.dtos.UpdateCompanyPaymentTypeRequestDTO;

import java.util.UUID;

@Tag(name = "company's payment type management", description = "Endpoints to list, create, update and delete company's on ERP system")
public interface CompanyPaymentTypeDocs {

    @Operation(summary = "List Paged Company's payment types", description = "Return a company's payment type list with pagination support and created date filter. \nRequires COMPANY_PAYMENT_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company's payment type retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have COMPANY_PAYMENT_READ authority", content = @Content)
    })
    ResponseEntity<Page<CompanyPaymentTypeDTO>> getAllCompanyPaymentTypes(@ParameterObject
                                                                          Pageable pageable,
                                                                          String searchTerm);

    @Operation(summary = "Create a company's payment type", description = "Create a new company's payment type with indications if the payment type is immediate, allows installments and card information's. Requires COMPANY_PAYMENT_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company's payment type created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyPaymentTypeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have COMPANY_PAYMENT_CREATE authority", content = @Content),
    })
    ResponseEntity<CompanyPaymentTypeDTO> create(CreateCompanyPaymentTypeRequestDTO data);

    @Operation(summary = "Update company's payment type data", description = "Update the indications of the payment type, if is immediate, allows installments and card information's of a specific company's payment type. \nRequires COMPANY_PAYMENT_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company's payment type updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyPaymentTypeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have COMPANY_PAYMENT_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Company's payment type not found", content = @Content)
    })
    ResponseEntity<CompanyPaymentTypeDTO> update(@Parameter(description = "UUID of the company's payment type to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                                 UUID id,
                                                 UpdateCompanyPaymentTypeRequestDTO data);

    @Operation(summary = "Remove company's payment type", description = "Inactivate a specific company's payment type from the system. \nRequires COMPANY_PAYMENT_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company's payment type deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyPaymentTypeDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have COMPANY_PAYMENT_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Company's payment type not found", content = @Content)
    })
    ResponseEntity<CompanyPaymentTypeDTO> delete(@Parameter(description = "UUID of the company's payment type to remove and reason of delete", example = "123e4567-e89b-12d3-a456-426614174000")
                                                 UUID id, DeleteCompanyPaymentTypeRequestDTO data);

}


