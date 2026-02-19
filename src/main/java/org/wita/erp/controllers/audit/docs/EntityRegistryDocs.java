package org.wita.erp.controllers.audit.docs;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.wita.erp.infra.config.audit.revision.GlobalRevisionDTO;
import org.wita.erp.infra.config.audit.revision.RevisionDTO;

import java.util.UUID;

@Tag(name = "entity's registry management", description = "Endpoints to list entity's registry on ERP system")
public interface EntityRegistryDocs {

    @Operation(summary = "List Paged registries", description = "Return a registries list with pagination support and revision filter. \nRequires LOG_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registry retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have LOG_READ authority", content = @Content)
    })
    ResponseEntity<Page<RevisionDTO<Object>>> getRevisions(@ParameterObject
                                              Pageable pageable,
                                                           @Parameter(description = "Filter registry by name")
                                                      @PathVariable String entity,
                                                           @Parameter(description = "Filter registry by id")
                                                      @PathVariable UUID id,
                                                           @Parameter(description = "Filter registry by revision number")
                                                      @RequestParam(required = false) Integer revision);

    @Operation(summary = "List Paged registries", description = "Return all registries list with pagination support. \nRequires LOG_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registry retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have LOG_READ authority", content = @Content)
    })
    ResponseEntity<Page<GlobalRevisionDTO>> getAllRevisions(
            @ParameterObject
            Pageable pageable
    );

}


