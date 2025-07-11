package project.authservice.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

@Data
@Entity
@Table( name = "users" )
public class User implements UserDetails
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column( nullable = false, unique = true )
    private String email;

    @Column( nullable = false )
    private String password;

    private boolean enabled;

    private String name;
    private String imageUrl;

    @Column( name = "oauth2_provider" )
    private String oauth2Provider;

    @Column( name = "oauth2_provider_id" )
    private String oauth2ProviderId;

    @ManyToMany( fetch = FetchType.EAGER )
    @JoinTable( name = "user_roles",
                joinColumns = @JoinColumn( name = "user_id" ),
                inverseJoinColumns = @JoinColumn( name = "role_id" ) )
    private Collection<Role> roles = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void addRole( Role role )
    {
        this.roles.add( role );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return this.roles;
    }

    @Override
    public String getUsername()
    {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }
}
