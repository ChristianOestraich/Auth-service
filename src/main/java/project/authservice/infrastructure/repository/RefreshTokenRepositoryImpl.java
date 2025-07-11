package project.authservice.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.authservice.domain.model.RefreshToken;
import project.authservice.domain.model.User;
import project.authservice.domain.repository.RefreshTokenRepository;

import java.util.Optional;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<RefreshToken> findByToken( String token )
    {
        return entityManager.createQuery(
                        "SELECT rt FROM RefreshToken rt WHERE rt.token = :token", RefreshToken.class )
                .setParameter( "token", token )
                .getResultStream()
                .findFirst();
    }

    @Override
    public RefreshToken save( RefreshToken refreshToken )
    {
        if ( refreshToken.getId() == null )
        {
            entityManager.persist( refreshToken );
            return refreshToken;
        }
        else
        {
            return entityManager.merge( refreshToken );
        }
    }

    @Override
    @Transactional
    public void deleteByUser( User user )
    {
        entityManager.createQuery("DELETE FROM RefreshToken rt WHERE rt.user = :user" )
                                     .setParameter( "user", user )
                                     .executeUpdate();
    }

    @Override
    @Transactional
    public void delete( RefreshToken token )
    {
        entityManager.remove( entityManager.contains( token ) ? token : entityManager.merge( token ) );
    }
}