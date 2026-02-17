package org.wita.erp.controllers.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.infra.config.audit.EntityRegistryService;
import org.wita.erp.infra.config.audit.revision.RevisionDTO;

import java.util.UUID;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class EntityRegistryController {
    private final EntityRegistryService entityRegistryService;

    @GetMapping("/{entity}/{id}")
    @PreAuthorize("hasAuthority('LOG_READ')")
    public ResponseEntity<Page<RevisionDTO<Object>>> getRevisions(@PageableDefault(size = 10, sort = "revision") Pageable pageable, @PathVariable String entity, @PathVariable UUID id, @RequestParam(required = false) Integer revision) {
        return ResponseEntity.ok(entityRegistryService.getRevisions(entity, id, revision, pageable));
    }

}

