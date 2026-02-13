package org.wita.erp.controllers.user.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.wita.erp.domain.entities.user.dtos.*;

import java.util.UUID;

@Tag(name = "auth", description = "Endpoints to register, login and reset user's password on ERP system")
public interface AuthenticationDocs {

    @Operation(summary = "Log in to the system with an user.", description = "Authenticate user and generate access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users logged successfully", content =  @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
    })
    ResponseEntity<LoginResponseDTO> login(AuthenticationDTO data);

    @Operation(summary = "Request a password recovery", description = "Request a password recovery email to reset user's password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recovery email sent successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecoveryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    ResponseEntity<RecoveryDTO> requestRecovery(RequestRecoveryDTO data, @Parameter(hidden = true) String userAgent) throws MessagingException;

    @Operation(summary = "Reset user's password", description = "Reset user's password using a valid recovery token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecoveryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    ResponseEntity<RecoveryDTO> resetPassword(RequestResetDTO data,
                                              @Schema(description = "Recovery token received by email", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                                              String token);

    @Operation(summary = "Register a new user on the system.", description = "Create a new user with name, email and password. \nRequires USER_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have USER_CREATE authority", content = @Content)
    })
    ResponseEntity<UserDTO> register(RegisterDTO data);
}


