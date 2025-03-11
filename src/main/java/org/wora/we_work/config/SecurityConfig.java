package org.wora.we_work.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/identity/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"verification-callback").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/identity/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/avis/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/avis/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/espaces-search/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/espaces/**").hasAuthority("ESPACE_UPDATE")
                        .requestMatchers(HttpMethod.POST, "/api/espaces/**").hasAuthority("ESPACE_CREATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/espaces/**").hasAuthority("ESPACE_DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/espaces/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/equipments/**").hasAuthority("EQUIPEMENT_CREATE")
                                .requestMatchers(HttpMethod.PUT, "/api/equipments/**").hasAuthority("EQUIPEMENT_UPDATE")
                                .requestMatchers(HttpMethod.DELETE, "/api/equipments/**").hasAuthority("EQUIPEMENT_DELETE")
                                .requestMatchers(HttpMethod.GET, "/api/equipments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/booking/**").hasAuthority("RESERVATION_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/booking/**").hasAuthority("RESERVATION_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/booking/**").hasAuthority("RESERVATION_DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/booking/**").hasAuthority("RESERVATION_READ")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, ex) -> {
                            log.error("Unauthorized error: {}", ex.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
                        }))
                .userDetailsService(customUserDetailsService)
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


}