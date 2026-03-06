package org.wita.erp.controllers.user.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.wita.erp.domain.entities.user.role.Permission;


@Tag(name = "permission management", description = "Endpoint to list permissions on ERP system")
public interface PermissionDocs {

    @Operation(summary = "List Paged Permissions", description = "Return a role list with pagination support and name filter. \nRequires PERMISSION_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissions retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have PERMISSION_READ authority", content = @Content)
    })
    ResponseEntity<Page<Permission>> getAllPermissions(
            @ParameterObject
            Pageable pageable,
            @Parameter(description = "Term used to filter permissions by name", example = "USER_READ")
            String searchTerm
    );
}


