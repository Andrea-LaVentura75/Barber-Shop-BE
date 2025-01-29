package it.epicode.Barber.Shop_BE.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        System.out.println("Richiesta ricevuta per URI: " + requestURI);

        // Ignora le rotte pubbliche
        if (requestURI.startsWith("/api/auth/login") || requestURI.startsWith("/api/auth/register")) {
            System.out.println("Rotta pubblica, nessun filtro JWT applicato.");
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        // Controlla se l'intestazione è presente e corretta
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                System.out.println("Username estratto dal token: " + username);
            } catch (IllegalArgumentException e) {
                System.out.println("Errore: Impossibile ottenere il token JWT");
            } catch (ExpiredJwtException e) {
                System.out.println("Errore: Il token JWT è scaduto");
            }
        } else {
            System.out.println("L'intestazione Authorization non è presente o non inizia con 'Bearer '");
        }

        // Verifica l'utente autenticato
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

            // Valida il token JWT
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("Token JWT valido. Utente autenticato: " + username);
            } else {
                System.out.println("Errore: Token JWT non valido per l'utente " + username);
            }
        } else if (username == null) {
            System.out.println("Errore: Impossibile autenticare l'utente. Username null.");
        }

        chain.doFilter(request, response);
    }



}
