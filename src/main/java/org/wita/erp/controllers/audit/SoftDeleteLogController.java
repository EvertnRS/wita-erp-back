package org.wita.erp.controllers.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wita.erp.domain.entities.audit.SoftDeleteLog;
import org.wita.erp.services.audit.SoftDeleteLogService;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class SoftDeleteLogController {
    private final SoftDeleteLogService softDeleteLogService;

    @GetMapping("/delete")
    @PreAuthorize("hasAuthority('LOG_READ')")
    public ResponseEntity<Page<SoftDeleteLog>> getAllTransactions(@PageableDefault(size = 10, sort = "deletedAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return softDeleteLogService.getAllSoftDeleteLogs(pageable, searchTerm);
    }
}
