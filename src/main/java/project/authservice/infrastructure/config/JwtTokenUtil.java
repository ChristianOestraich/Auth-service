package project.authservice.infrastructure.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil
{
    @Value( "${auth.jwt.secret}" )
    private String secret;

    @Value("${auth.jwt.expiration}")
    private Long expiration;

    public String getUsernameFromToken( String token )
    {
        return getClaimFromToken( token, Claims::getSubject );
    }

    public Date getExpirationDateFromToken( String token )
    {
        return getClaimFromToken( token, Claims::getExpiration );
    }

    public <T> T getClaimFromToken( String token, Function<Claims, T> claimsResolver )
    {
        final Claims claims = getAllClaimsFromToken( token );
        return claimsResolver.apply( claims );
    }

    private Claims getAllClaimsFromToken( String token )
    {
        return Jwts.parser().setSigningKey( secret ).parseClaimsJws( token ).getBody();
    }

    private Boolean isTokenExpired( String token )
    {
        final Date expiration = getExpirationDateFromToken( token );
        return expiration.before( new Date() );
    }

    public String generateToken( UserDetails userDetails )
    {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken( claims, userDetails.getUsername() );
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // Use a chave segura
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey) // Use a chave segura
                .compact();
    }

    public Boolean validateToken( String token, UserDetails userDetails )
    {
        final String username = getUsernameFromToken( token );
        return ( username.equals( userDetails.getUsername() ) && !isTokenExpired( token ) );
    }
}