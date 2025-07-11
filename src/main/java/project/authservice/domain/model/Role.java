package project.authservice.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@Entity
@Table( name = "roles" )
public class Role implements GrantedAuthority
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column( nullable = false, unique = true )
    private String name;

    @Override
    public String getAuthority()
    {
        return "";
    }
}
