package com.rafael.usuario.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // ==================== ROTAS PÚBLICAS (mantido do seu código original) ====================
        if ((requestURI.equals("/usuarios") && "POST".equals(request.getMethod())) ||
                requestURI.equals("/usuarios/login")) {

            chain.doFilter(request, response);
            return;
        }

        // ==================== ROTAS PROTEGIDAS ====================
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String token = authorizationHeader.substring(7);

            try {
                final String username = jwtUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // ==================== NOVO: Suporte a Token de Serviço ====================
                    if (jwtUtil.isServiceToken(token)) {

                        // Autentica como token de serviço (sem carregar usuário do banco)
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken("service-account", null, Collections.emptyList());

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                    } else {
                        // ==================== FLUXO ORIGINAL DE USUÁRIO ====================
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        if (jwtUtil.validateToken(token, username)) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            } catch (Exception e) {
                // Token inválido ou expirado
            }
        }

        chain.doFilter(request, response);
    }
}