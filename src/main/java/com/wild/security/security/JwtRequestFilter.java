package com.wild.security.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wild.security.jwt.JwtUtilities;
import com.wild.security.service.CustomizedUserDetailsService;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtUtilities jwtUtilities;
    private CustomizedUserDetailsService userDetailsService;

    public JwtRequestFilter(JwtUtilities jwtUtilities, CustomizedUserDetailsService userDetailsService) {
        this.jwtUtilities = jwtUtilities;
        this.userDetailsService = userDetailsService;
    }

    @Override
    //méthode qui s'active à chaque requête
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
            System.out.println("@@@@@@@@@@@ " + authorizationHeader);

        String username = null;
        String token = null;

        // 1. Récupération du token et du username dans le header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            
            username = jwtUtilities.extractUsername(token);
        }

         // 2. Vérification du token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtilities.validateToken(token, userDetails)) {
                // UsernamePasswordAuthenticationToken est un container comprenant les informations de l'utilisateur dans le contexte de sécurité 
                var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, // Le User
                null, // Le mdp mais pas besoin ici
                userDetails.getAuthorities() // les Rôles
                );
                
                usernamePasswordAuthenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
                );
                System.out.println("@@@@@@@@@@@ " + usernamePasswordAuthenticationToken);
                // On ajoute UsernamePasswordAuthenticationToken avec le setAuthentication
                SecurityContextHolder
                .getContext()
                .setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

}
