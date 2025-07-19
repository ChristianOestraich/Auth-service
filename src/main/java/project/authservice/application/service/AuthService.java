package project.authservice.application.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.authservice.application.dto.TokenResponse;
import project.authservice.domain.model.RefreshToken;
import project.authservice.domain.model.User;
import project.authservice.domain.repository.UserRepository;
import project.authservice.infrastructure.config.JwtTokenUtil;
import project.authservice.infrastructure.exception.AuthException;
import project.authservice.infrastructure.exception.TokenRefreshException;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenUtil jwtTokenUtil,
                       UserDetailsService userDetailsService,
                       UserRepository userRepository,
                       RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TokenResponse authenticate(String email, String password) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthException("Usuário não encontrado"));

            if (!user.isEnabled()) {
                throw new AuthException("Conta desativada");
            }

            System.out.println("Senha digitada: " + password);
            System.out.println("Senha armazenada (hash): " + user.getPassword());

            // Usando o método de verificação de senha
            if (verifyPassword(password, user.getPassword())) {
                throw new AuthException("Credenciais inválidas");
            }

            // Carrega os detalhes do usuário
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String token = jwtTokenUtil.generateToken(userDetails);

            return new TokenResponse(token);

        } catch (BadCredentialsException e) {
            throw new AuthException("Credenciais inválidas");
        } catch (Exception e) {
            throw new AuthException("Erro durante a autenticação: " + e.getMessage());
        }
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        // Comparar a senha digitada com o hash armazenado
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    @Transactional
    public TokenResponse refreshToken(String refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenRequest)
                .orElseThrow(() -> new TokenRefreshException("Refresh token não encontrado"));

        refreshToken = refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        String newToken = jwtTokenUtil.generateToken(user);

        return new TokenResponse(newToken, refreshTokenRequest);
    }

    @Transactional
    public void logout(String authHeader) {
        String token = authHeader.substring(7);

        String email = jwtTokenUtil.getUsernameFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Usuário não encontrado"));

        refreshTokenService.deleteByUserId(user.getId());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Usuário não encontrado"));
    }
}