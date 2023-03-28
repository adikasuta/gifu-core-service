package com.gifu.coreservice.filter;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.repository.UserRepository;
import com.gifu.coreservice.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;

@Configuration
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepo;

    private final Collection<String> excludeUrlPatterns = new ArrayList<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtFilter(UserRepository userRepo) {
        this.userRepo = userRepo;
        this.excludeUrlPatterns.add("/hello");
        this.excludeUrlPatterns.add("/api/public/**");
        this.excludeUrlPatterns.add("/api/auth/change-password");
        this.excludeUrlPatterns.add("/api/auth/signup");
        this.excludeUrlPatterns.add("/api/auth/login");
        this.excludeUrlPatterns.add("/api/auth/logout");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return excludeUrlPatterns.stream()
                .anyMatch(p -> pathMatcher.match(p, request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        try {
            Jws<Claims> claimsJws = JwtUtils.parseJwt(token);
            // Get user identity and set it on the spring security context
            User userDetails = userRepo
                    .findByEmail(claimsJws.getBody().get("email", String.class)).orElseThrow(
                            () -> new UsernameNotFoundException("User is not found")
                    );

            UsernamePasswordAuthenticationToken
                    authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null,
                    userDetails.getAuthorities()
            );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (Exception e) {
            chain.doFilter(request, response);
        }


    }
}
