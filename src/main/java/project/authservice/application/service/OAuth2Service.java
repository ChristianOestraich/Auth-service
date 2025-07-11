package project.authservice.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.authservice.domain.model.User;
import project.authservice.domain.repository.UserRepository;
import project.authservice.infrastructure.client.FacebookOAuth2Client;
import project.authservice.infrastructure.client.GoogleOAuth2Client;
import project.authservice.infrastructure.config.JwtTokenUtil;

import java.util.Map;
import java.util.UUID;

@Service
public class OAuth2Service {
    private final GoogleOAuth2Client googleOAuth2Client;
    private final FacebookOAuth2Client facebookOAuth2Client;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public OAuth2Service( GoogleOAuth2Client googleOAuth2Client,
                          FacebookOAuth2Client facebookOAuth2Client,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenUtil jwtTokenUtil )
    {
        this.googleOAuth2Client = googleOAuth2Client;
        this.facebookOAuth2Client = facebookOAuth2Client;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public String authenticateOAuth2User( String provider, String code )
    {
        Map<String, Object> userAttributes = getOAuth2UserAttributes( provider, code );
        User user = processOAuth2User( provider, userAttributes );
        return jwtTokenUtil.generateToken( user );
    }

    private Map<String, Object> getOAuth2UserAttributes( String provider, String code )
    {
        return switch ( provider.toLowerCase() )
        {
            case "google" -> googleOAuth2Client.getUserInfo( code );
            case "facebook" -> facebookOAuth2Client.getUserInfo( code );
            default -> throw new IllegalArgumentException( "Provedor OAuth2 não suportado: " + provider );
        };
    }

    private User processOAuth2User( String provider, Map<String, Object> attributes )
    {
        String email = (String) attributes.get( "email" );
        String providerId = (String) attributes.get( "sub" ) != null ?
                (String) attributes.get( "sub" ) :
                (String) attributes.get( "id" );

        return userRepository.findByEmail( email )
                .orElseGet( () -> registerNewOAuth2User( provider, providerId, email, attributes ) );
    }

    private User registerNewOAuth2User( String provider, String providerId, String email,
                                        Map<String, Object> attributes )
    {
        User user = new User();
        user.setEmail( email );
        user.setPassword( passwordEncoder.encode( UUID.randomUUID().toString() ) );
        user.setEnabled( true );
        user.setOauth2Provider( provider );
        user.setOauth2ProviderId( providerId );
        user.setName( (String) attributes.get( "name" ) );
        user.setImageUrl( (String) attributes.get( "picture" ) );

        return userRepository.save( user );
    }

    public String buildAuthorizationUrl( String provider )
    {
        return switch ( provider.toLowerCase() )
        {
            case "google" -> googleOAuth2Client.buildAuthorizationUrl();
            case "facebook" -> facebookOAuth2Client.buildAuthorizationUrl();
            default -> throw new IllegalArgumentException( "Provedor OAuth2 não suportado: " + provider );
        };
    }
}