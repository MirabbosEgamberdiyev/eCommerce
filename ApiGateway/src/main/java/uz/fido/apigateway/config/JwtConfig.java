package uz.fido.apigateway.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    private String secret;
    private Long expiration;
    private PublicEndpoints publicEndpoints;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public PublicEndpoints getPublicEndpoints() {
        return publicEndpoints;
    }

    public void setPublicEndpoints(PublicEndpoints publicEndpoints) {
        this.publicEndpoints = publicEndpoints;
    }

    public static class PublicEndpoints {
        private List<String> endpoints;

        public List<String> getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(List<String> endpoints) {
            this.endpoints = endpoints;
        }
    }

    @PostConstruct
    public void init() {
        logger.info("JwtConfig initialized: secret={}, expiration={}, publicEndpoints={}",
                secret, expiration, publicEndpoints != null ? publicEndpoints.getEndpoints() : null);
    }
}