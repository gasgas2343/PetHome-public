package com.system.shop.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Order(1)
public class RateLimitFilter implements Filter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 只擋搶購 API
        if (!request.getRequestURI().contains("/grab")) {
            chain.doFilter(req, res);
            return;
        }

        // 同一 IP，10 秒內最多 5 次
        String key = "rate:ip:" + request.getRemoteAddr();
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, 10, TimeUnit.SECONDS);
        }

        if (count > 5) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"請求太頻繁，請稍後再試\"}");
            return;
        }

        chain.doFilter(req, res);
    }
}