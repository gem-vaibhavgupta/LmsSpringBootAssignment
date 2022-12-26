package com.gemini.LMS.service;

import com.gemini.LMS.model.UserCreds;
import com.gemini.LMS.model.UserInfo;
import com.gemini.LMS.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public final class LoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    public LoginService(final AuthenticationManager authenticationManager,
                        final JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public UserInfo loginUser(final UserCreds userCreds) {
        LOGGER.info("Logging in user -> {} ", userCreds.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userCreds.getUsername(), userCreds.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(userDetails.getUsername());
        userInfo.setEmail(userDetails.getEmail());
        userInfo.setRole(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")));
        userInfo.setToken(jwtTokenUtils.generateToken(userInfo));
        return userInfo;
    }
}
