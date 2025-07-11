package project.authservice.infrastructure.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter
{
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    public JwtRequestFilter( JwtTokenUtil jwtTokenUtil,
                             UserDetailsService userDetailsService )
    {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain ) throws ServletException, IOException
    {
        final String requestTokenHeader = request.getHeader( "Authorization" );

        String username = null;
        String jwtToken = null;

        if ( requestTokenHeader != null && requestTokenHeader.startsWith( "Bearer " ) )
        {
            jwtToken = requestTokenHeader.substring( 7 );
            try
            {
                username = jwtTokenUtil.getUsernameFromToken( jwtToken );
            }
            catch ( IllegalArgumentException e )
            {
                logger.error( "Não foi possível obter o token JWT" );
            }
            catch ( ExpiredJwtException e )
            {
                logger.warn( "Token JWT expirado" );
            }
        }

        if ( username != null && SecurityContextHolder.getContext().getAuthentication() == null )
        {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername( username );

            if ( jwtTokenUtil.validateToken( jwtToken, userDetails ) )
            {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken( userDetails, null,
                                                                 userDetails.getAuthorities() );

                authentication.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );

                SecurityContextHolder.getContext().setAuthentication( authentication );
            }
        }
        chain.doFilter(request, response);
    }
}