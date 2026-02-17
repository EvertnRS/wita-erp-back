package org.wita.erp.services.audit.observer;

public record SoftDeleteLogObserver(String entityId, String entityType, String reason) {
}

