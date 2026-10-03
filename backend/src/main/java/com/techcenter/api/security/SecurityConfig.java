package com.techcenter.api.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Permite solicitudes preflight OPTIONS requeridas por el navegador
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(authorizationRequestResolver(clientRegistrationRepository)))
                        .loginPage("/login")
                        .defaultSuccessUrl("/oauth2/google/success", true));
        return http.build();
    }

    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(jakarta.servlet.http.HttpServletRequest request) {
                return personalizar(resolver.resolve(request), request);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(jakarta.servlet.http.HttpServletRequest request,
                                                      String clientRegistrationId) {
                return personalizar(resolver.resolve(request, clientRegistrationId), request);
            }
        };
    }

    private OAuth2AuthorizationRequest personalizar(OAuth2AuthorizationRequest authorizationRequest,
                                                    jakarta.servlet.http.HttpServletRequest request) {
        if (authorizationRequest == null || !"select_account".equals(request.getParameter("prompt"))) {
            return authorizationRequest;
        }
        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(parameters -> parameters.put("prompt", "select_account"))
                .build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();

        // Incluye http://localhost (puerto 80) y http://localhost:8080 para Docker Compose
        config.setAllowedOrigins(java.util.List.of(
                "http://localhost",
                "http://localhost:80",
                "http://localhost:8080",
                "http://localhost:4200"
        ));

        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        config.setExposedHeaders(java.util.List.of("Set-Cookie", "Authorization"));
        config.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}