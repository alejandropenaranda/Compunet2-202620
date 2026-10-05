package com.compunet.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. SecurityFilterChain: Filtros de rutas y Login Custom
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas accesibles sin autenticación
                .requestMatchers("/login", "/css/**", "/js/**", "/public/**").permitAll()
                // Toda otra ruta requiere autenticación obligatoria
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .logout(Customizer.withDefaults())
            // .formLogin(form -> form
            //     .loginPage("/login")
            //     .defaultSuccessUrl("/usuarios", true)
            //     .permitAll()
            // )
            // .logout(logout -> logout
            //     .logoutSuccessUrl("/login?logout")
            //     .permitAll()
            // )
            .build();
    }

    // 2. PasswordEncoder: Hashing adaptativo con BCrypt
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }

    // 3. UserDetailsService: Proveedor de usuarios en memoria para Sesión 1
    // @Bean
    // public UserDetailsService userDetailsService() {
    //     UserDetails profesor = User.builder()
    //         .username("docente@icesi.edu.co")
    //         .password(passwordEncoder().encode("profesor123"))
    //         .roles("PROFESOR")
    //         .build();

    //     return new InMemoryUserDetailsManager(profesor);
    // }
}
