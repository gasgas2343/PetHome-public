package com.system.member.security;

import com.system.member.entity.UserBean;
import com.system.member.repository.UsersRepository;
import com.system.member.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UsersRepository userBeanRepository;

    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long userid = jwtService.getUserId(token);

            Integer tokenVersion = jwtService.getTokenVersion(token);
            Integer permissionVersion = jwtService.getPermissionVersion(token);

            UserBean user = userBeanRepository.findById(userid)
                    .orElse(null);

            if (user == null) {
                writeUnauthorized(response, "USER_NOT_FOUND", "找不到使用者");
                return;
            }

            if (user.getTokenVersion() != tokenVersion) {
                writeUnauthorized(response, "TOKEN_VERSION_EXPIRED", "登入狀態已失效，請重新登入");
                return;
            }

            if (user.getPermissionVersion() != permissionVersion) {
                writeUnauthorized(response, "PERMISSION_VERSION_EXPIRED", "權限已更新，請重新登入");
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userid,
                            null,
                            List.of()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();

            writeUnauthorized(response, "TOKEN_INVALID", "Token 無效或已過期");
            return;
        }
        filterChain.doFilter(request, response);
    }


    private void writeUnauthorized(HttpServletResponse response,
                                   String code, String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String body = String.format("""
                {
                  "success": false,
                  "code": "%s",
                  "message": "%s",
                  "data": null
                }
                """, code, message);

        response.getWriter().write(body);
    }
}
