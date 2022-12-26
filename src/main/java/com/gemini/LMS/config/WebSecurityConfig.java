package com.gemini.LMS.config;

import com.gemini.LMS.filter.JwtVerificationFilter;
import com.gemini.LMS.service.UserDetailServiceImpl;
import com.gemini.LMS.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfiguration {

    private final JwtVerificationFilter jwtVerificationFilter;
    private final UserDetailServiceImpl userDetailsService;

    public WebSecurityConfig(final JwtVerificationFilter jwtVerificationFilter, final UserDetailServiceImpl userDetailsService) {
        this.jwtVerificationFilter = jwtVerificationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and().authorizeHttpRequests()
                .requestMatchers(HttpMethod.DELETE).hasRole(Constants.Roles.Admin.name())
                .requestMatchers(HttpMethod.POST).hasRole(Constants.Roles.Admin.name())
                .requestMatchers(HttpMethod.GET).hasAnyRole(Constants.Roles.Admin.name(),
                        Constants.Roles.Staff.name(), Constants.Roles.User.name())
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtVerificationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
