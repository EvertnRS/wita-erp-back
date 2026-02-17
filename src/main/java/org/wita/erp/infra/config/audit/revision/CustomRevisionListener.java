package org.wita.erp.infra.config.audit.revision;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.user.UserRepository;

import java.util.UUID;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity revision =
                (CustomRevisionEntity) revisionEntity;

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {

            String email = authentication.getName();

            UserRepository userRepository =
                    ApplicationContext.getBean(UserRepository.class);

            UUID userId = userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElse(null);

            revision.setUserId(userId);
        }
    }

}

