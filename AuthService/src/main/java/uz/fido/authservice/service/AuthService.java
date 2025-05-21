package uz.fido.authservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uz.fido.authservice.dto.LoginRequest;
import uz.fido.authservice.dto.RegisterRequest;
import uz.fido.authservice.dto.AuthResponse;
import uz.fido.authservice.model.User;
import uz.fido.authservice.repository.UserRepository;
import uz.fido.authservice.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.ArrayList;

@Service
public class AuthService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        logger.debug("User found: {}", username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        logger.info("Registering user: {}", registerRequest.getUsername());
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        userRepository.save(user);

        String token = jwtService.generateToken(registerRequest.getUsername());
        logger.debug("Generated JWT for user: {}", registerRequest.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        logger.info("Authenticating user: {}", loginRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                                                                          );
        logger.debug("Authentication successful for user: {}", loginRequest.getUsername());

        String token = jwtService.generateToken(loginRequest.getUsername());
        logger.debug("Generated JWT for user: {}", loginRequest.getUsername());
        return new AuthResponse(token);
    }

    public Authentication getAuthentication(String username) {
        logger.debug("Getting authentication for user: {}", username);
        UserDetails userDetails = loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}