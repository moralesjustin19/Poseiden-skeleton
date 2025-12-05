package com.nnk.springboot.config;

import com.nnk.springboot.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration de Spring Security
 * Définit les règles d'authentification et d'autorisation
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Configure le PasswordEncoder pour BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure l'AuthenticationManager pour utiliser notre UserDetailsService
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    /**
     * Configure les règles de sécurité HTTP
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // Ressources publiques
                .antMatchers("/css/**", "/js/**").permitAll()
                // Routes d'administration réservées aux ADMIN
                .antMatchers("/user/**").hasRole("ADMIN")
                // Toutes les autres routes nécessitent une authentification
                .anyRequest().authenticated()
                .and()
            .formLogin()
                // Page de login personnalisée
                .loginPage("/app/login")
                .defaultSuccessUrl("/bidList/list", true)
                .permitAll()
                .and()
            .logout()
                .logoutSuccessUrl("/app/login?logout")
                .permitAll()
                .and()
            .exceptionHandling()
                // Page d'erreur pour accès refusé
                .accessDeniedPage("/app/error");
    }
}

