package com.georent.security;

import com.georent.config.JwtConfigurationProperties;
import com.georent.domain.GeoRentUserDetails;
import com.georent.message.Message;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    public static final String TOKEN_PREFIX = "Bearer ";

    private final transient JwtConfigurationProperties jwtProperties;

    @Autowired
    public JwtProvider(final JwtConfigurationProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(final GeoRentUserDetails userPrincipal) {
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiresIn());
        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getUserId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .compact();
    }

    public Long getUserIdFromToken(final String token) {
        final Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public String getTokenFromRequest(final HttpServletRequest request) {
        final String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(jwt) && jwt.startsWith(TOKEN_PREFIX)) {
            return jwt.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public Boolean tokenIsValid(final String token) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException
                | UnsupportedJwtException | IllegalArgumentException ex) {
            log.error("Validate token failed {}", Message.INVALID_TOKEN_ERROR.getDescription());
            return false;
        }
    }
}

