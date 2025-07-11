package project.authservice.adapter.in.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.authservice.application.dto.TokenResponse;
import project.authservice.application.service.OAuth2Service;

@RestController
@RequestMapping( "/oauth2" )
public class OAuth2Controller
{
    private final OAuth2Service oAuth2Service;

    public OAuth2Controller( OAuth2Service oAuth2Service )
    {
        this.oAuth2Service = oAuth2Service;
    }

    @GetMapping( "/authorize/{provider}" )
    public ResponseEntity<Void> authorize( @PathVariable String provider )
    {
        String authorizationUrl = oAuth2Service.buildAuthorizationUrl( provider );
        return ResponseEntity.status( 302 ).header( "Location", authorizationUrl ).build();
    }

    @GetMapping( "/callback/{provider}" )
    public ResponseEntity<TokenResponse> callback( @PathVariable String provider,
                                                   @RequestParam String code )
    {
        String token = oAuth2Service.authenticateOAuth2User( provider, code );
        return ResponseEntity.ok( new TokenResponse( token, null ) );
    }
}
