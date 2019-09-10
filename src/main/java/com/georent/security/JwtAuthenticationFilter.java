package com.georent.security;

import com.georent.service.GeoRentUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final transient JwtProvider jwtProvider;
    private final transient GeoRentUserDetailsService detailsService;
    private final transient JwtEntryPointUnauthorizedHandler authEntryPoint;

    @Autowired
    public JwtAuthenticationFilter(final JwtProvider jwtProvider,
                                   final GeoRentUserDetailsService detailsService,
                                   final JwtEntryPointUnauthorizedHandler authEntryPoint) {
        this.jwtProvider = jwtProvider;
        this.detailsService = detailsService;
        this.authEntryPoint = authEntryPoint;
    }

    @Override
    public void doFilterInternal(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final FilterChain filterChain) throws IOException, ServletException {
        try {
            final String accessToken = jwtProvider.getTokenFromRequest(request);
            if (StringUtils.hasText(accessToken) && jwtProvider.tokenIsValid(accessToken)) {
                Long userId = jwtProvider.getUserIdFromToken(accessToken);
                UserDetails userDetails = detailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
//            authEntryPoint.commence(request, response, ex);
        }
        filterChain.doFilter(request, response);
    }
}
