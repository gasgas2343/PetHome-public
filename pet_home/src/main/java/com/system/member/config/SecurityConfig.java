package com.system.member.config;

import com.system.member.security.JWTAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//下面是邱雅家的不要刪
import org.springframework.http.HttpMethod;
import java.util.List;

@Configuration
public class SecurityConfig {
        @Autowired
        private JWTAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(configurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // 以下不需要登入可操作權限
                                                .requestMatchers(
                                                                "/auth/register",
                                                                "/auth/login",
                                                                "/auth/login/totp",
                                                                "/auth/refresh",
                                                                "/auth/logout",
                                                                "/auth/verify-email",
                                                                "/auth/send-email-verification",
                                                                "/auth/forgot-password",
                                                                "/auth/reset-password",
                                                                "/auth/email-change-confirm",
                                                                "/flash-sales/active")
                                                .permitAll()
                                                // 商品公開查詢（GET only）
                                                .requestMatchers(HttpMethod.GET, "/products", "/products/**")
                                                .permitAll()
                                                .requestMatchers("/payments/ecpay/notify").permitAll()
                                                .requestMatchers("/brands/**").permitAll()
                                                .requestMatchers("/issue/**").permitAll()
                                                .requestMatchers("/active/**").permitAll() // 如果 active、skus 也是首頁要用的公開資料
                                                .requestMatchers("/skus/**").permitAll()
                                                // 上方是邱雅的別刪

                                                .requestMatchers(
                                                                // 會員
                                                                "/users/me",
                                                                "/users/me/**",
                                                                // 後台
                                                                "/admin/**",
                                                                // 訂單
                                                                "/orders/user/**",
                                                                "/orders/create",
                                                                "/orders/*/items",
                                                                "/orders/*",
                                                                // 購物車
                                                                "/cart/user/**",
                                                                "/cart/add",
                                                                "/cart/*/sku/*",
                                                                "/cart/*/clear",
                                                                // 優惠券顧客端
                                                                "/customer/coupons/my",
                                                                "/customer/coupons/claim/**",
                                                                "/coupons/user/**",
                                                                // 收藏清單
                                                                "/wishlist/**")
                                                .authenticated()

                                                // wash使用
                                                .requestMatchers("/pay/confirm").permitAll()
                                                .requestMatchers("/ajax/pages/**", "/api/calendars/**",
                                                                "/api/calendars", "/api/periodOfDays",
                                                                "/api/schedules", "/api/schedules/**",
                                                                "/api/wash-package-items", "/api/wash-services")
                                                .permitAll()
                                                // wash使用

                                                // ===== 商城後台（STAFF + ADMIN）=====
                                                // ===== 商城後台（STAFF + ADMIN）=====
                                                .requestMatchers(HttpMethod.POST, "/products", "/products/upload")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/products/*", "/products/*/status",
                                                                "/products/skus/*")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/products/*").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/orders", "/flash-sales/all")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.PATCH, "/orders/*/status").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/payments/all", "/payments/order/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.POST, "/payments/mock-pay",
                                                                "/payments/create")
                                                .authenticated()
                                                .requestMatchers(
                                                                "/admin/coupons/**",
                                                                "/coupons", "/coupons/issue",
                                                                "/flash-sales/create", "/flash-sales/update",
                                                                "/flash-sales/delete/**",
                                                                "/flash-sales/*/grab")
                                                .authenticated()
                                                // 上面是邱雅的別刪
                                                // .anyRequest().permitAll()
                                                // 沒列入的全擋
                                                .anyRequest().authenticated())
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.getWriter().write("""
                                                                        {
                                                                          "success": false,
                                                                          "code": "AUTH_REQUIRED",
                                                                          "message": "請先登入",
                                                                          "data": null
                                                                        }
                                                                        """);
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.getWriter().write("""
                                                                        {
                                                                          "success": false,
                                                                          "code": "ACCESS_DENIED",
                                                                          "message": "權限不足",
                                                                          "data": null
                                                                        }
                                                                        """);
                                                }))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable());

                return http.build();
        }

        @Bean
        public CorsConfigurationSource configurationSource() {
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(List.of("http://localhost:5173","http://192.168.36.143:5173"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }
}
