package org.wita.erp.controllers.audit.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.wita.erp.domain.entities.audit.SoftDeleteLog;

@Tag(name = "soft delete logs management", description = "Endpoints to list soft delete logs on ERP system")
public interface SoftDeleteLogDocs {

    @Operation(summary = "List all soft delete logs", description = "Returns a list of all soft delete logs with pagination support and created date filter.  \nRequires LOG_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Soft delete logs retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have LOG_READ authority")
    })
    ResponseEntity<Page<SoftDeleteLog>> getAllTransactions(@PageableDefault(size = 10, sort = "deletedAt") Pageable pageable,
                                                           @Parameter(description = "Term used to filter log by entity's name", example = "users")
                                                           @RequestParam(required = false) String searchTerm);

}


