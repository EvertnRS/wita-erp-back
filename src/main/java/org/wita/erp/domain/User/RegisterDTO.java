package org.wita.erp.domain.User;

public record RegisterDTO(String email, String name, String password, Long role) {
}
