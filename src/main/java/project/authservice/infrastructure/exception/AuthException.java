package project.authservice.infrastructure.exception;

public class AuthException extends RuntimeException
{
    public AuthException( String message )
    {
        super( message );
    }
}