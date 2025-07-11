package project.authservice.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class GoogleOAuth2Client
{
    @Value( "${spring.security.oauth2.client.registration.google.client-id}" )
    private String clientId;

    @Value( "${spring.security.oauth2.client.registration.google.client-secret}" )
    private String clientSecret;

    @Value( "${spring.security.oauth2.client.registration.google.redirect-uri}" )
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public String buildAuthorizationUrl()
    {
        return UriComponentsBuilder.fromHttpUrl( "https://accounts.google.com/o/oauth2/v2/auth" )
                                   .queryParam( "client_id", clientId )
                                   .queryParam( "redirect_uri", redirectUri )
                                   .queryParam( "response_type", "code" )
                                   .queryParam( "scope", "email profile" )
                                   .queryParam( "access_type", "offline" )
                                   .toUriString();
    }

    public Map<String, Object> getUserInfo( String code )
    {
        String token = getAccessToken( code );
        return getUserProfile( token );
    }

    private String getAccessToken( String code )
    {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        Map<String, String> params = Map.of( "code", code,
                                             "client_id", clientId,
                                             "client_secret", clientSecret,
                                             "redirect_uri", redirectUri,
                                             "grant_type", "authorization_code" );

        Map<String, Object> response = restTemplate.postForObject( tokenUrl, params, Map.class );
        return (String) response.get( "access_token" );
    }

    private Map<String, Object> getUserProfile( String accessToken )
    {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        return restTemplate.getForObject( userInfoUrl + "?access_token=" + accessToken, Map.class );
    }
}