package project.authservice.infrastructure.exception;

public class TokenRefreshException extends RuntimeException
{
    public TokenRefreshException( String message )
    {
        super( String.format( "Falha no refresh token [%s]: %s",  message ) );
    }
}