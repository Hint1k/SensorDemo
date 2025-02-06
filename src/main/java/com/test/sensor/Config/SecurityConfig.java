package com.test.sensor.Config;

import com.test.sensor.jwt.JwtAuthenticationFilter;
import com.test.sensor.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final DataSource securityDataSource;
    private final JwtService jwtService;

    @Autowired
    public SecurityConfig(DataSource securityDataSource, JwtService jwtService) {
        this.securityDataSource = securityDataSource;
        this.jwtService = jwtService;
    }

    @Bean
    public JdbcUserDetailsManager user() {
        return new JdbcUserDetailsManager(securityDataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests((authorize) -> authorize
                            // Exclude the /auth/login endpoint from JWT validation
                            .requestMatchers("/auth/login").permitAll() // Allow access to the login endpoint

                            // Exclude Swagger UI and API docs from JWT validation (public access)
                            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/")
                            .permitAll()

                            // Grant both ADMIN and MANAGER roles access to all application endpoints
                            .requestMatchers("/measurements/").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers("/measurements/add").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers("/measurements/rainyDaysCount").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers("/sensors/registration").hasAnyRole("ADMIN", "MANAGER")

                            // Any other request requires authentication
                            .anyRequest().authenticated()
                    )
                    // Add a JWT filter for token authentication
                    .addFilterBefore(new JwtAuthenticationFilter(jwtService),
                            UsernamePasswordAuthenticationFilter.class);

            return http.build();
        } catch (Exception e) {
            log.error("Error configuring security filter chain", e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) {
        AuthenticationManagerBuilder authenticationManager = http.getSharedObject(AuthenticationManagerBuilder.class);
        try {
            authenticationManager.jdbcAuthentication()
                    .dataSource(securityDataSource).passwordEncoder(passwordEncoder());
            return authenticationManager.build();
        } catch (Exception e) {
            log.error("Error in AuthenticationManager", e);
            throw new RuntimeException(e);
        }
    }
}