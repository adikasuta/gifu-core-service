package com.gifu.coreservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final Collection<String> excludeUrlPatterns = new ArrayList<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String BEARER = "BEARER";

    public JwtFilter() {
        this.excludeUrlPatterns.add("/swagger-ui/**");
        this.excludeUrlPatterns.add("/health");
        this.excludeUrlPatterns.add("/internal/api/v1/**");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            log.info("Calling: " + request.getRequestURL());
            String cookie = request.getHeader("cookie");
            List<HttpCookie> parsed = HttpCookie.parse(cookie);
            String bearer = getBearer(parsed);
//            jwtAuthorizationService.validateJwt(bearer);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT AUTH FAILED " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

    }

    private String getBearer(List<HttpCookie> cookies) {
        for (HttpCookie it : cookies) {
            if (BEARER.equals(it.getName())) {
                return it.getValue();
            }
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return excludeUrlPatterns.stream()
                .anyMatch(p -> pathMatcher.match(p, request.getRequestURI()));
    }


    @Bean
    public FilterRegistrationBean jwtAuthFilter() {
        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(new JwtFilter());
        filterRegBean.setEnabled(Boolean.TRUE);
        filterRegBean.setName("staticKeyFilter");
        filterRegBean.setOrder(1);
        return filterRegBean;
    }
}
