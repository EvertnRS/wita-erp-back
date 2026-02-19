package org.wita.erp.infra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.wita.erp.infra.providers.twofactor.TwoFactorAuthenticationToken;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfigurations {
    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(HttpMethod.POST,
                                "/auth/login",
                                "/auth/recovery",
                                "/auth/reset"
                        ).permitAll()

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/*.css",
                                "/static/**",
                                "/custom-swagger.css"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/auth/login/2fa/confirm"
                        ).access((authentication, context) ->
                                new AuthorizationDecision(authentication.get() instanceof TwoFactorAuthenticationToken)
                        )

                        .anyRequest().access((authentication, context) -> {
                            var auth = authentication.get();

                            if (auth instanceof TwoFactorAuthenticationToken) {
                                return new AuthorizationDecision(false);
                            }

                            return new AuthorizationDecision(auth.isAuthenticated());
                        })
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class).build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
