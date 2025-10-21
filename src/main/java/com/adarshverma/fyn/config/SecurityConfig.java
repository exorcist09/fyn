package com.adarshverma.fyn.config;


import com.adarshverma.fyn.security.JwtRequestFilter;
import com.adarshverma.fyn.service.AppUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserDetailService appUserDetailService;

    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Configure Spring Security filter chain.
     * <p>
     * Hinglish (detailed):
     * Ye method Spring Security ka main configuration return karta hai.
     * - CORS ko default se enable karta hai.
     * - CSRF(Cross-Site Request Forgery) ko disable karta hai kyunki hum stateless (token based) API use kar rahe honge.
     * - Kuch endpoints ("/status", "/health", "/register", "/activate", "/login") ko sabke liye open (permitAll) rakhta hai.
     * - Baaki saari requests ke liye authentication required hai.
     * - Session policy ko STATELESS set karta hai: server side session maintain nahi hogi (JWT/Token based flows ke liye).
     * <p>
     * Notes:
     * - Agar future mein custom authentication/authorization filters add karne ho to yahin chain mein add karo.
     * - Production mein endpoints aur rules ko aur restrictive banane ka dhyan rakho.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/status", "/health", "/register", "/activate", "/login").permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    /**
     * Provide BCrypt password encoder.
     * <p>
     * Hinglish (detailed):
     * Ye bean password hashing ke liye BCryptPasswordEncoder provide karta hai.
     * - User passwords ko DB mein plain text mein store mat karo; hamesha encode karke store karo.
     * - BCrypt adaptive hashing use karta hai, jo brute-force se bachata hai.
     * - Agar strength (work factor) change karna ho to yahan constructor mein integer pass kar sakte ho.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define CORS configuration source.
     * <p>
     * Hinglish (detailed):
     * Ye bean CORS policies define karta hai jo application pe apply hongi.
     * - Allowed origins: '*' (sabhi origins allowed) — development convenience ke liye; production mein specific origins set karo.
     * - Allowed methods: GET, POST, PUT, DELETE, OPTIONS.
     * - Allowed headers: Authorization, Content-Type, Accept (agar custom headers chahiye to add karo).
     * - Allow credentials: true (agar cookies/credentials share karne hain).
     * - Ye configuration sabhi paths ('/**') pe register ki gayi hai.
     * <p>
     * Notes:
     * - Security ke nazariye se wildcard origins aur allowCredentials=true combine karna unsafe ho sakta hai.
     * Production mein specific origin list aur stricter header/method policies use karo.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(appUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }


}
