package project.authservice.domain.repository;

import project.authservice.domain.model.RefreshToken;
import project.authservice.domain.model.User;

import java.util.Optional;

public interface RefreshTokenRepository
{
    Optional<RefreshToken> findByToken( String token );
    RefreshToken save( RefreshToken refreshToken );
    void deleteByUser( User user );
    void delete( RefreshToken token );
}