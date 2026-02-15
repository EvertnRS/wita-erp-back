package org.wita.erp.domain.entities.user.role.dtos;

import java.util.Set;

public record UpdateRoleRequestDTO(String name, Set<Long> permissions) {}
