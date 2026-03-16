package com.duoc.seguridadcalidad;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Description;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests((requests) -> requests
            .requestMatchers("/", "/home", "/login", "/**.css").permitAll()
            
            // EL VETERINARIO Y ADMIN gestionan pacientes
            .requestMatchers("/patients/**").hasAnyRole("ADMIN", "VETERINARIO")
            
            // LA RECEPCIONISTA Y ADMIN gestionan citas
            .requestMatchers("/appointments/**").hasAnyRole("ADMIN", "RECEPCIONISTA")
            
            .anyRequest().authenticated()
        )
            .formLogin((form) -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout((logout) -> logout.permitAll());

        return http.build();
    }

    
    @Bean
    @Description("In memory Userdetails service registered since DB doesn't have user table ")
    public UserDetailsService users() {
        // The builder will ensure the passwords are encoded before saving in memory
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN").build();

        UserDetails vet = User.builder()
            .username("vet01")
            .password(passwordEncoder().encode("vet123"))
            .roles("VETERINARIO").build();

        UserDetails recep = User.builder()
            .username("recep01")
            .password(passwordEncoder().encode("recep123"))
            .roles("RECEPCIONISTA").build();

        return new InMemoryUserDetailsManager(admin, vet, recep);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

