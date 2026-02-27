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
import org.wita.erp.domain.entities.user.dtos.DeleteUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;

import java.util.UUID;

@Tag(name = "user management", description = "Endpoints to list, update and delete users on ERP system")
public interface UserDocs {

    @Operation(summary = "List Paged Users", description = "Return a user list with pagination support and name filter. \nRequires USER_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have USER_READ authority", content = @Content)
    })
    ResponseEntity<Page<UserDTO>> getAllUsers(@ParameterObject
                                              Pageable pageable,
                                              @Parameter(description = "Term used to filter users by name or email.", example = "John Doe")
                                              String searchTerm);

    @Operation(summary = "Update user's data", description = "Update the name, email, or password of a specific user. \nRequires USER_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have USER_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    ResponseEntity<UserDTO> update(@Parameter(description = "UUID of the user to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                   UUID id,
                                   UpdateUserRequestDTO data);

    @Operation(summary = "Remove user", description = "Inactivate a specific user from the system. \nRequires USER_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have USER_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    ResponseEntity<UserDTO> delete(@Parameter(description = "UUID of the user to remove and reason of delete", example = "123e4567-e89b-12d3-a456-426614174000")
                                   UUID id, DeleteUserRequestDTO data);

}


