package com.example.proyecto1bases.config;

import com.example.proyecto1bases.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security 6.
 *
 * Roles:
 *   - ROLE_ADMINISTRADOR → /admin/**
 *   - ROLE_JUGADOR       → /jugador/**
 *   - Público            → /login, /registro, recursos estáticos
 *
 * @Lazy en UsuarioService rompe la dependencia circular:
 *   SecurityConfig → UsuarioService → PasswordEncoder → SecurityConfig
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** @Lazy para evitar dependencia circular con UsuarioService */
    @Autowired
    @Lazy
    private UsuarioService usuarioService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // recursos públicos
                .requestMatchers("/login", "/registro",
                                 "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // solo ADMINISTRADOR
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                // solo JUGADOR
                .requestMatchers("/jugador/**").hasRole("JUGADOR")
                // el resto requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")          // Spring Security procesa el POST aquí
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
