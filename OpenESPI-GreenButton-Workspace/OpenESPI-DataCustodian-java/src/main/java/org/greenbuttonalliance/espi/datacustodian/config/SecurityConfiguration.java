/*
 *
 *    Copyright (c) 2018-2025 Green Button Alliance, Inc.
 *
 *    Portions (c) 2013-2018 EnergyOS.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package org.greenbuttonalliance.espi.datacustodian.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the OpenESPI Data Custodian Resource Server.
 * 
 * This configuration implements OAuth2 Resource Server security using JWT tokens
 * from the separate OpenESPI Authorization Server. It replaces the legacy Spring
 * Security OAuth 2.0.2.RELEASE XML configuration with modern Spring Security 6.5.
 * 
 * Key Features:
 * - OAuth2 Resource Server with JWT validation
 * - ESPI-specific authorization rules
 * - CORS configuration for web clients
 * - Method-level security for service layers
 * - Multi-environment JWT configuration
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    @Value("${espi.authorization-server.issuer-uri}")
    private String issuerUri;

    @Value("${espi.authorization-server.jwk-set-uri}")
    private String jwkSetUri;

    /**
     * Main security filter chain for ESPI Resource Server endpoints.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // Disable CSRF for API endpoints
            .csrf(AbstractHttpConfigurer::disable)
            
            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure session management (stateless for OAuth2)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info",
                    "/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/h2-console/**"
                ).permitAll()
                
                // ESPI root resource endpoints (require authentication)
                .requestMatchers(HttpMethod.GET, "/espi/1_1/resource/ApplicationInformation/**")
                    .hasAnyAuthority("SCOPE_DataCustodian_Admin_Access", "SCOPE_ThirdParty_Admin_Access")
                
                .requestMatchers(HttpMethod.GET, "/espi/1_1/resource/Authorization/**")
                    .hasAnyAuthority("SCOPE_DataCustodian_Admin_Access")
                
                // ESPI Usage Point endpoints
                .requestMatchers(HttpMethod.GET, "/espi/1_1/resource/UsagePoint/**")
                    .hasAnyAuthority(
                        "SCOPE_FB_15_READ_3rd_party", 
                        "SCOPE_FB_16_READ_3rd_party", 
                        "SCOPE_FB_36_READ_3rd_party",
                        "SCOPE_DataCustodian_Admin_Access"
                    )
                
                .requestMatchers(HttpMethod.POST, "/espi/1_1/resource/UsagePoint/**")
                    .hasAnyAuthority(
                        "SCOPE_FB_15_WRITE_3rd_party", 
                        "SCOPE_FB_16_WRITE_3rd_party", 
                        "SCOPE_FB_36_WRITE_3rd_party",
                        "SCOPE_DataCustodian_Admin_Access"
                    )
                
                // ESPI Subscription-based endpoints
                .requestMatchers(HttpMethod.GET, "/espi/1_1/resource/Subscription/*/UsagePoint/**")
                    .hasAnyAuthority(
                        "SCOPE_FB_15_READ_3rd_party", 
                        "SCOPE_FB_16_READ_3rd_party", 
                        "SCOPE_FB_36_READ_3rd_party"
                    )
                
                .requestMatchers(HttpMethod.POST, "/espi/1_1/resource/Subscription/*/UsagePoint/**")
                    .hasAnyAuthority(
                        "SCOPE_FB_15_WRITE_3rd_party", 
                        "SCOPE_FB_16_WRITE_3rd_party", 
                        "SCOPE_FB_36_WRITE_3rd_party"
                    )
                
                // ESPI Meter Reading endpoints
                .requestMatchers(HttpMethod.GET, "/espi/1_1/resource/**/MeterReading/**")
                    .hasAnyAuthority(
                        "SCOPE_FB_15_READ_3rd_party", 
                        "SCOPE_FB_16_READ_3rd_party", 
                        "SCOPE_FB_36_READ_3rd_party",
                        "SCOPE_DataCustodian_Admin_Access"
                    )
                
                .requestMatchers(HttpMethod.POST, "/espi/1_1/resource/**/MeterReading/**")
                    .hasAnyAuthority(
                        "SCOPE_FB_15_WRITE_3rd_party", 
                        "SCOPE_FB_16_WRITE_3rd_party", 
                        "SCOPE_FB_36_WRITE_3rd_party",
                        "SCOPE_DataCustodian_Admin_Access"
                    )
                
                // ESPI Interval Reading endpoints
                .requestMatchers(HttpMethod.GET, "/espi/1_1/resource/**/IntervalReading/**")
                    .hasAnyAuthority(
                        "SCOPE_FB_15_READ_3rd_party", 
                        "SCOPE_FB_16_READ_3rd_party", 
                        "SCOPE_FB_36_READ_3rd_party",
                        "SCOPE_DataCustodian_Admin_Access"
                    )
                
                // ESPI Batch endpoints
                .requestMatchers(HttpMethod.GET, "/espi/1_1/resource/Batch/**")
                    .hasAnyAuthority(
                        "SCOPE_FB_15_READ_3rd_party", 
                        "SCOPE_FB_16_READ_3rd_party", 
                        "SCOPE_FB_36_READ_3rd_party",
                        "SCOPE_DataCustodian_Admin_Access"
                    )
                
                // Admin endpoints
                .requestMatchers("/admin/**")
                    .hasAuthority("SCOPE_DataCustodian_Admin_Access")
                
                // All other ESPI endpoints require authentication
                .requestMatchers("/espi/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Configure OAuth2 Resource Server
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            
            // Allow H2 console in local development
            .headers(headers -> headers
                .frameOptions().sameOrigin()
            )
            
            .build();
    }

    /**
     * JWT Decoder with validation for the Authorization Server.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
            .withJwkSetUri(jwkSetUri)
            .jwsAlgorithm(org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.RS256)
            .cache(Duration.ofMinutes(10))
            .build();

        OAuth2TokenValidator<Jwt> withIssuer = new JwtIssuerValidator(issuerUri);
        OAuth2TokenValidator<Jwt> withTimestamp = new JwtTimestampValidator();
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withIssuer, withTimestamp);
        
        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }

    /**
     * JWT Authentication Converter for ESPI scope authorities.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("SCOPE_");
        authoritiesConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return jwtConverter;
    }

    /**
     * CORS configuration for web clients.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "https://localhost:*",
            "https://*.greenbuttonalliance.org"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Total-Count"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}