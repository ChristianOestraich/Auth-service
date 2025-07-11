package project.authservice.infrastructure.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
public class FeignConfig
{
    @Bean
    public RequestInterceptor requestInterceptor()
    {
        return requestTemplate ->
        {
            if ( SecurityContextHolder.getContext().getAuthentication() != null
                    && SecurityContextHolder.getContext().getAuthentication().getCredentials() instanceof Jwt )
            {
                Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
                requestTemplate.header( "Authorization", "Bearer " + jwt.getTokenValue() );
            }
        };
    }
}