package org.wita.erp.infra.config.audit.revision;

import java.time.Instant;
import java.util.UUID;

public record GlobalRevisionDTO(
        Number revision,
        Instant revisionDate,
        UUID userId,
        String entityName,
        UUID entityId,
        Integer revisionType
) {}

