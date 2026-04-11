package com.karan.ecommerce_app.security;


import com.karan.ecommerce_app.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component  // responsible for setting authentication object inside the security context after
// token validation from authorization header
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token=null;
        String email=null;

        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            email = jwtService.extractEmail(token);

        }
        if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(email); // throws userName not found exception itself
            if(jwtService.isTokenValid(token,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}



//Flow:
// We need to apply this jwt filter before username password filter runs and sets authentication object inside the security context
// This filter will run on every API request once!
// Get authHeader-> extract (token,email)-> if email exists in DB->Check if token is valid ->set authentication object in security contexts
