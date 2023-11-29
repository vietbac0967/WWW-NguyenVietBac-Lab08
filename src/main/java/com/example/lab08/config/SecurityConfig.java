package com.example.lab08.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import javax.sql.DataSource;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                DataSource dataSource) throws Exception {
        PasswordEncoder encoder = passwordEncoder();
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .withDefaultSchema()
                .withUser(User.withUsername("user")
                        .password(encoder.encode("user"))
                        .roles("USER"))
                .withUser(
                        User.withUsername("admin")
                                .password(encoder.encode("admin"))
                                .roles("ADMIN", "USER")
                )
        ;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/home").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/v1/auth/**", "/v2/api-docs/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/swagger-ui/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .csrf(AbstractHttpConfigurer::disable)//for POST/PUT/DELETE swagger
                .httpBasic(Customizer.withDefaults())
        ;
        //for POST/PUT/DELETE swagger
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfiguration()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration() {//for POST/PUT/DELETE swagger
        return request -> {
            org.springframework.web.cors.CorsConfiguration config =
                    new org.springframework.web.cors.CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            return config;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
