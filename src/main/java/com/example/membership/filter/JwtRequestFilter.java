package com.example.membership.filter;

import com.example.membership.model.entity.User;
import com.example.membership.repository.UserRepository;
import com.example.membership.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 对于登录和注册请求，直接放行
        if (request.getServletPath().equals("/api/user/login") ||
            request.getServletPath().equals("/api/user/register")){
            chain.doFilter(request, response);
            return;
        }

        try {
            final String authorizationHeader = request.getHeader("Authorization");
            log.debug("Authorization header: {}", authorizationHeader);

            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
                log.debug("Extracted username from JWT: {}", username);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                log.debug("Loaded user details for username: {}", username);

                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("用户不存在"));

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authentication set for user: {}", username);
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                }
            }
            
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT认证过程中发生错误: {}", e.getMessage());
            
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "认证失败: " + e.getMessage());
            
            objectMapper.writeValue(response.getWriter(), errorResponse);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/user/login") || path.equals("/api/user/register");
    }
} 