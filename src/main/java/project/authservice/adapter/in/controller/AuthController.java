package project.authservice.adapter.in.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.authservice.application.dto.LoginRequest;
import project.authservice.application.dto.RefreshTokenRequest;
import project.authservice.application.dto.RegisterRequest;
import project.authservice.application.dto.TokenResponse;
import project.authservice.application.service.AuthService;
import project.authservice.application.service.UserRegistrationService;

@RestController
@RequestMapping( "/api/auth" )
public class AuthController
{
    private final AuthService authService;
    private final UserRegistrationService registrationService;

    public AuthController( AuthService authService,
                           UserRegistrationService registrationService )
    {
        this.authService = authService;
        this.registrationService = registrationService;
    }

    @PostMapping( "/login" )
    public ResponseEntity<TokenResponse> login( @Valid @RequestBody LoginRequest loginRequest )
    {
        TokenResponse tokenResponse = authService.authenticate( loginRequest.getEmail(),
                                                                loginRequest.getPassword() );
        return ResponseEntity.ok( tokenResponse );
    }

    @PostMapping( "/register" )
    public ResponseEntity<Void> register( @Valid @RequestBody RegisterRequest registerRequest )
    {
        registrationService.registerUser( registerRequest );
        return ResponseEntity.ok().build();
    }

    @PostMapping( "/refresh" )
    public ResponseEntity<TokenResponse> refreshToken( @Valid @RequestBody RefreshTokenRequest request )
    {
        TokenResponse tokenResponse = authService.refreshToken( request.getRefreshToken() );
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping( "/logout" )
    public ResponseEntity<Void> logout( @RequestHeader( "Authorization" ) String token )
    {
        authService.logout( token );
        return ResponseEntity.ok().build();
    }
}