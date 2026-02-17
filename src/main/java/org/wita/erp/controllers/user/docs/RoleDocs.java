package org.wita.erp.controllers.user.docs;

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
import org.wita.erp.domain.entities.user.dtos.RoleDTO;
import org.wita.erp.domain.entities.user.role.dtos.CreateRoleRequestDTO;
import org.wita.erp.domain.entities.user.role.dtos.DeleteRoleRequestDTO;
import org.wita.erp.domain.entities.user.role.dtos.UpdateRoleRequestDTO;


@Tag(name = "role management", description = "Endpoints to list, create, update and delete roles on ERP system")
public interface RoleDocs {

    @Operation(summary = "List Paged Roles", description = "Return a role list with pagination support and name filter. \nRequires ROLE_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ROLE_READ authority", content = @Content)
    })
    ResponseEntity<Page<RoleDTO>> getAllRoles(
            @ParameterObject
            Pageable pageable,
            @Parameter(description = "Term used to filter roles by name", example = "Admin")
            String searchTerm
    );

    @Operation(summary = "Create a role", description = "Create a new role with a name and a set of permissions. Requires ROLE_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ROLE_CREATE authority", content = @Content),
    })
    ResponseEntity<RoleDTO> create(CreateRoleRequestDTO data);

    @Operation(summary = "Update role's data", description = "Update the name or permissions of a specific role. Requires ROLE_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ROLE_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    ResponseEntity<RoleDTO> update(@Parameter(description = "Id of the role to update", example = "2")
                                   Long id,
                                   UpdateRoleRequestDTO data);

    @Operation(summary = "Remove role", description = "Inactivate a specific role from the system. Requires ROLE_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ROLE_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    ResponseEntity<RoleDTO> delete(@Parameter(description = "Id of the role to remove and reason of delete", example = "2")
                                   Long id, DeleteRoleRequestDTO data);

}


