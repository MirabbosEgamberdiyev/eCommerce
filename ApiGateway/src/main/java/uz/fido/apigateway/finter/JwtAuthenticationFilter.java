package uz.fido.apigateway.finter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import uz.fido.apigateway.config.JwtConfig;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final static String BEARER_PREFIX = "Bearer ";
    private final static AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtConfig jwtConfig;
    private Key signingKey;

    @Autowired
    public JwtAuthenticationFilter(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @PostConstruct
    public void init() {
        if (jwtConfig.getSecret() == null) {
            logger.error("JWT secret is not configured");
            throw new IllegalStateException("JWT secret must be configured");
        }
        this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        logger.info("JWT signing key initialized successfully");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        logger.debug("Processing request for path: {}", path);

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            logger.debug("Public endpoint detected, skipping JWT validation: {}", path);
            return chain.filter(exchange);
        }

        // Check for Authorization header
        List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.get(0).startsWith(BEARER_PREFIX)) {
            logger.warn("Missing or invalid Authorization header for path: {}", path);
            return setErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        // Validate JWT
        String token = authHeaders.get(0).substring(BEARER_PREFIX.length());
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.debug("JWT token validated successfully for path: {}. Subject: {}", path, claims.getSubject());

            // Add user info to request headers for downstream services
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r.headers(headers -> headers.add("X-User-Id", claims.getSubject())))
                    .build();

            return chain.filter(modifiedExchange);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired for path: {}. Subject: {}", path, e.getClaims().getSubject());
            return setErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "JWT token expired");
        } catch (JwtException e) {
            logger.warn("Invalid JWT token for path: {}. Error: {}", path, e.getMessage());
            return setErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during JWT validation for path: {}. Error: {}", path, e.getMessage(), e);
            return setErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during authentication");
        }
    }

    private boolean isPublicEndpoint(String path) {
        if (path.endsWith("/api-docs") || path.startsWith("/swagger-ui")) {
            logger.debug("Bypassing JWT validation for API docs or Swagger UI: {}", path);
            return true;
        }
        if (jwtConfig.getPublicEndpoints() == null || jwtConfig.getPublicEndpoints().getEndpoints() == null) {
            logger.warn("No public endpoints configured for path: {}", path);
            return false;
        }
        List<String> publicEndpoints = jwtConfig.getPublicEndpoints().getEndpoints();
        logger.debug("Checking path {} against public endpoints: {}", path, publicEndpoints);
        return publicEndpoints.stream()
                .anyMatch(pattern -> {
                    boolean match = PATH_MATCHER.match(pattern, path);
                    logger.debug("Path {} {} pattern {}", path, match ? "matches" : "does not match", pattern);
                    return match;
                });
    }

    private Mono<Void> setErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"error\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)))
                .then(exchange.getResponse().setComplete());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}