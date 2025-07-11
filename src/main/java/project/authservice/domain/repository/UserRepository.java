package project.authservice.domain.repository;

import project.authservice.domain.model.User;

import java.util.Optional;

public interface UserRepository
{
    Optional<User> findByEmail( String email );
    Optional<User> findById( Long userId );
    User save( User user );
    boolean existsByEmail( String email );
}