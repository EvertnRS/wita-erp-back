package org.wita.erp.domain.user.dtos;

import java.util.Set;

public record UpdateRoleRequestDTO(String name, Set<Long> permissions) {}
