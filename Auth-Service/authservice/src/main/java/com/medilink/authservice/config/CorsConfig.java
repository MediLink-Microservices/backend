package com.medilink.authservice.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${cors.allowed-origins}") String allowedOrigins,
            @Value("${cors.allowed-methods}") String allowedMethods,
            @Value("${cors.allowed-headers}") String allowedHeaders,
            @Value("${cors.allow-credentials}") boolean allowCredentials,
            @Value("${cors.max-age}") long maxAge) {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(parseList(allowedOrigins));
        configuration.setAllowedMethods(parseList(allowedMethods));
        configuration.setAllowedHeaders(parseList(allowedHeaders));
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> parseList(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .collect(Collectors.toList());
    }
}
