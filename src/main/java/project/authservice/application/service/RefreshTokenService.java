package project.authservice.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.authservice.domain.model.RefreshToken;
import project.authservice.domain.model.User;
import project.authservice.domain.repository.RefreshTokenRepository;
import project.authservice.domain.repository.UserRepository;
import project.authservice.infrastructure.exception.TokenRefreshException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService
{
    @Value( "${auth.jwt.refresh-expiration}" )
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService( RefreshTokenRepository refreshTokenRepository,
                                UserRepository userRepository )
    {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken( String token )
    {
        return refreshTokenRepository.findByToken( token );
    }

    public RefreshToken createRefreshToken( String email )
    {
        User user = userRepository.findByEmail( email )
                .orElseThrow( () -> new RuntimeException( "Usuário não encontrado com email: " + email ) );

        refreshTokenRepository.deleteByUser( user );

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser( user );
        refreshToken.setToken( UUID.randomUUID().toString() );
        refreshToken.setExpiryDate( Instant.now().plusMillis( refreshTokenDurationMs ) );

        return refreshTokenRepository.save( refreshToken );
    }

    public RefreshToken verifyExpiration( RefreshToken token )
    {
        if ( token.getExpiryDate().compareTo( Instant.now() ) < 0 )
        {
            refreshTokenRepository.delete( token );
            throw new TokenRefreshException( token.getToken() );
        }
        return token;
    }

    @Transactional
    public void deleteByUserId( Long userId )
    {
        User user = userRepository.findById( userId )
                .orElseThrow( () -> new RuntimeException( "Usuário não encontrado com ID: " + userId ) );
        refreshTokenRepository.deleteByUser( user );
    }
}