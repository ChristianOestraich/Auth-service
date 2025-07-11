package project.authservice.application.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
public class AuthService
{
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthService( AuthenticationManager authenticationManager,
                        JwtTokenUtil jwtTokenUtil,
                        UserDetailsService userDetailsService,
                        UserRepository userRepository,
                        RefreshTokenService refreshTokenService )
    {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public TokenResponse authenticate( String email, String password )
    {
        try
        {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken( email, password ) );

            SecurityContextHolder.getContext().setAuthentication( authentication );

            UserDetails userDetails = userDetailsService.loadUserByUsername( email );

            String token = jwtTokenUtil.generateToken( userDetails );

            RefreshToken refreshToken = refreshTokenService.createRefreshToken( email );

            return new TokenResponse( token, refreshToken.getToken() );

        }
        catch ( BadCredentialsException e )
        {
            throw new AuthException( "Credenciais inválidas" );
        }
    }

    @Transactional
    public TokenResponse refreshToken( String refreshTokenRequest )
    {
        RefreshToken refreshToken = refreshTokenService.findByToken( refreshTokenRequest )
                .orElseThrow(() -> new TokenRefreshException( "Refresh token não encontrado" ) );

        refreshToken = refreshTokenService.verifyExpiration( refreshToken );

        User user = refreshToken.getUser();

        String newToken = jwtTokenUtil.generateToken( user );

        return new TokenResponse( newToken, refreshTokenRequest );
    }

    @Transactional
    public void logout( String authHeader )
    {
        String token = authHeader.substring( 7 );

        String email = jwtTokenUtil.getUsernameFromToken( token );

        User user = userRepository.findByEmail( email )
                .orElseThrow( () -> new AuthException( "Usuário não encontrado" ) );

        refreshTokenService.deleteByUserId( user.getId() );
    }

    public User getCurrentUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail( email )
                .orElseThrow( () -> new AuthException( "Usuário não encontrado" ) );
    }
}