package org.wita.erp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encode {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pass = encoder.encode("123");
        System.out.println(pass);
    }
}
