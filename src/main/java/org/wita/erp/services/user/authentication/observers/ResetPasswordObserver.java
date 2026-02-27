package org.wita.erp.services.user.authentication.observers;

import org.wita.erp.domain.entities.user.User;

public record ResetPasswordObserver(User user, String newPassword) {
}

