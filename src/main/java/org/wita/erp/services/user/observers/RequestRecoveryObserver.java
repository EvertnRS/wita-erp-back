package org.wita.erp.services.user.observers;

import org.wita.erp.domain.entities.user.User;

import java.time.LocalDateTime;

public record RequestRecoveryObserver(User user, String encodedToken, LocalDateTime expiresAt) {
}

