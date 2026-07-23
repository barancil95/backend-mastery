package com.baranproject.backendmastery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Konfigürasyon Sınıfı (Spring Boot 3 + Spring Security 6)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Şifreleri güvenli bir şekilde BCrypt algoritması ile hash'leyen bileşen.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * HTTP Güvenlik Filtre Zinciri (İzin ve yetki kuralları).
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Statik dosyalar ve genel sayfalar HERKESE AÇIK
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // 2. Ürün ekleme/düzenleme/silme SADECE ADMIN'E AÇIK
                        .requestMatchers("/products/new", "/products/edit/**", "/products/delete/**").hasRole("ADMIN")

                        // 3. Ürün listesini görmek HERKESE AÇIK
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()

                        // 4. Sipariş işlemleri SADECE GİRİŞ YAPMIŞ KULLANICILARA AÇIK
                        .requestMatchers("/orders/**").authenticated()

                        // 5. Diğer tüm istekler için GİRİŞ ŞARTI
                        .anyRequest().authenticated())
                // Form bazlı giriş ayarları
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/products")
                        .permitAll())
                // Çıkış (Logout) ayarları
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }
}
