package project.authservice.application.service;

import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.authservice.application.dto.RegisterRequest;
import project.authservice.application.dto.UserDto;
import project.authservice.domain.event.UserRegisteredEvent;
import project.authservice.domain.model.Role;
import project.authservice.domain.model.User;
import project.authservice.domain.repository.UserRepository;
import project.authservice.infrastructure.client.UserServiceClient;

import java.util.Collections;

@Service
public class UserRegistrationService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final UserServiceClient userServiceClient;

    public UserRegistrationService( UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    ApplicationEventPublisher eventPublisher,
                                    UserServiceClient userServiceClient )
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    public User registerUser( @Valid RegisterRequest registerRequest )
    {
        if ( userRepository.existsByEmail( registerRequest.getEmail() ) )
        {
            throw new RuntimeException( "Email already in use" );
        }

        User user = new User();
        user.setEmail( registerRequest.getEmail() );
        user.setPassword( passwordEncoder.encode( registerRequest.getPassword() ) );
        user.setEnabled( true );

        Role role = new Role();
        role.setName( "ROLE_CLIENT" );
        user.setRoles( Collections.singleton( role ) );

        User savedUser = userRepository.save( user );

        eventPublisher.publishEvent( new UserRegisteredEvent( savedUser ) );

        userServiceClient.createUser( convertToUserDto( savedUser ) );

        return savedUser;
    }

    private UserDto convertToUserDto( User user )
    {
        return UserDto.builder().email( user.getEmail() )
                                .roles( user.getRoles() )
                                .build();
    }
}
