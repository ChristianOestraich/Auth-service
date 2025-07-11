package project.authservice.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import project.authservice.domain.model.User;
import project.authservice.domain.repository.UserRepository;

@Repository
public class UserRepositoryImpl implements UserRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findByEmail( String email )
    {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email", User.class )
                            .setParameter( "email", email )
                            .getResultStream()
                            .findFirst();
    }

    @Override
    public Optional<User> findById( Long userId )
    {
        try
        {
            User user = entityManager.find( User.class, userId );
            return Optional.ofNullable( user );
        }
        catch ( Exception e )
        {
            return Optional.empty();
        }
    }

    @Override
    public User save( User user )
    {
        if ( user.getId() == null )
        {
            entityManager.persist( user );
            return user;
        }
        else
        {
            return entityManager.merge( user );
        }
    }

    @Override
    public boolean existsByEmail( String email )
    {
        return findByEmail( email ).isPresent();
    }
}