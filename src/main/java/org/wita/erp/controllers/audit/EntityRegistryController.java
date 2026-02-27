package org.wita.erp.controllers.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.controllers.audit.docs.EntityRegistryDocs;
import org.wita.erp.infra.config.audit.EntityRegistryService;
import org.wita.erp.infra.config.audit.revision.GlobalRevisionDTO;
import org.wita.erp.infra.config.audit.revision.RevisionDTO;

import java.util.UUID;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class EntityRegistryController implements EntityRegistryDocs {
    private final EntityRegistryService entityRegistryService;

    @GetMapping("/{entity}/{id}")
    @PreAuthorize("hasAuthority('LOG_READ')")
    public ResponseEntity<Page<RevisionDTO<Object>>> getRevisions(@PageableDefault(size = 10, sort = "revision") Pageable pageable, @PathVariable String entity, @PathVariable UUID id, @RequestParam(required = false) Integer revision) {
        return ResponseEntity.ok(entityRegistryService.getRevisions(entity, id, revision, pageable));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LOG_READ')")
    public ResponseEntity<Page<GlobalRevisionDTO>> getAllRevisions(
            @PageableDefault(size = 10, sort = "revisionDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(entityRegistryService.getAllRevisions(pageable));
    }


}

