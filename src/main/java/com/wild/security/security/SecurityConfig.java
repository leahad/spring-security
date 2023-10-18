package com.wild.security.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/login", "/register").permitAll()
                .requestMatchers("/user-admin").hasAnyAuthority("USER", "ADMIN")
                .requestMatchers("/admin").hasAuthority("ADMIN")
                .requestMatchers("/multipass").authenticated()
			)
			 .csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // You can disable csrf protection by removing this line
                .ignoringRequestMatchers("/register", "/login")
                .disable()  // Décommentez pour désactiver en entier la protection CSRF en développement
                )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)// Spring Security ne crée pas de session
            // Nous allons utiliser JWT pour gérer les sessions
            );
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build(); // Très important de retourner le résultat de la méthode build() sinon rien de tout ça ne fonctionne !
    }

}