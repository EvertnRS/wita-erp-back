package org.wita.erp.infra.config.audit.revision;

import org.hibernate.envers.RevisionType;

import java.time.Instant;
import java.util.UUID;

public record RevisionDTO<T>(
        T entity,
        Number revision,
        Instant revisionDate,
        RevisionType revisionType,
        UUID user
) {}

