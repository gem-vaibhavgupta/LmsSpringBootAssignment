package com.gemini.LMS.filter;

import com.gemini.LMS.model.UserInfo;
import com.gemini.LMS.service.UserDetailServiceImpl;
import com.gemini.LMS.utils.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final UserDetailServiceImpl userDetailService;
    private final JwtTokenUtils jwtTokenUtils;

    public JwtVerificationFilter(final UserDetailServiceImpl userDetailService, final JwtTokenUtils jwtTokenUtils) {
        this.userDetailService = userDetailService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwtToken;
        String authorizationHeader = request.getHeader("Authorization");
        UserInfo userInfo = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            userInfo = jwtTokenUtils.parseToken(jwtToken);
        }

        if (userInfo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailService.loadUserByUsername(userInfo.getUsername());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

}