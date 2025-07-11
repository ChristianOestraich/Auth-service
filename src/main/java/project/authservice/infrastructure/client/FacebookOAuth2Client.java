package project.authservice.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class FacebookOAuth2Client
{
    @Value( "${spring.security.oauth2.client.registration.facebook.client-id}" )
    private String clientId;

    @Value( "${spring.security.oauth2.client.registration.facebook.client-secret}" )
    private String clientSecret;

    @Value( "${spring.security.oauth2.client.registration.facebook.redirect-uri}" )
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public String buildAuthorizationUrl()
    {
        return UriComponentsBuilder.fromHttpUrl( "https://www.facebook.com/v12.0/dialog/oauth" )
                .queryParam( "client_id", clientId )
                .queryParam( "redirect_uri", redirectUri )
                .queryParam( "response_type", "code" )
                .queryParam( "scope", "email,public_profile" )
                .toUriString();
    }

    public Map<String, Object> getUserInfo( String code )
    {
        String token = getAccessToken( code );
        return getUserProfile( token );
    }

    private String getAccessToken( String code )
    {
        String tokenUrl = "https://graph.facebook.com/v12.0/oauth/access_token";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl( tokenUrl )
                                                           .queryParam( "client_id", clientId )
                                                           .queryParam( "client_secret", clientSecret )
                                                           .queryParam( "redirect_uri", redirectUri )
                                                           .queryParam( "code", code );

        Map<String, Object> response = restTemplate.getForObject (builder.toUriString(), Map.class );
        return (String) response.get( "access_token" );
    }

    private Map<String, Object> getUserProfile( String accessToken )
    {
        String userInfoUrl = "https://graph.facebook.com/v12.0/me";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl( userInfoUrl)
                                                           .queryParam( "fields", "id,name,email,picture" )
                                                           .queryParam( "access_token", accessToken );

        return restTemplate.getForObject( builder.toUriString(), Map.class );
    }
}