package br.com.autocarehub.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.cors(cors -> {})
        .csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(
                        "/api/v1/auth/login",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/openapi.yaml")
                    .permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/demo-leads")
                    .permitAll()
                    .requestMatchers(
                        org.springframework.http.HttpMethod.GET,
                        "/api/v1/customers/*/service-orders",
                        "/api/v1/customers/*/vehicles")
                    .authenticated()
                    .requestMatchers(
                        org.springframework.http.HttpMethod.GET,
                        "/api/v1/customers",
                        "/api/v1/customers/*")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.GET,
                        "/api/v1/vehicles",
                        "/api/v1/vehicles/*",
                        "/api/v1/workshop-services",
                        "/api/v1/workshop-services/*",
                        "/api/v1/parts",
                        "/api/v1/parts/*",
                        "/api/v1/service-orders",
                        "/api/v1/service-orders/metrics/average-execution-time")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/demo-leads")
                    .hasAuthority("ROLE_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.POST,
                        "/api/v1/workshop-services",
                        "/api/v1/parts")
                    .hasAuthority("ROLE_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.POST,
                        "/api/v1/customers",
                        "/api/v1/vehicles")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.POST, "/api/v1/service-orders")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.PUT,
                        "/api/v1/customers/*",
                        "/api/v1/vehicles/*",
                        "/api/v1/workshop-services/*",
                        "/api/v1/parts/*")
                    .hasAuthority("ROLE_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.PATCH,
                        "/api/v1/parts/*/stock",
                        "/api/v1/service-orders/*/status")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.DELETE,
                        "/api/v1/customers/*",
                        "/api/v1/vehicles/*",
                        "/api/v1/workshop-services/*",
                        "/api/v1/parts/*")
                    .hasAuthority("ROLE_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.GET, "/api/v1/service-orders/*")
                    .authenticated()
                    .requestMatchers(
                        org.springframework.http.HttpMethod.POST,
                        "/api/v1/service-orders/*/budget/approve")
                    .authenticated()
                    .requestMatchers("/api/v1/**")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:5173");
    configuration.addAllowedOrigin("http://127.0.0.1:5173");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
