package org.wita.erp.domain.User.Dtos;

import java.util.Set;

public record UpdateRoleRequestDTO(String name, Set<Long> permissions) {}
