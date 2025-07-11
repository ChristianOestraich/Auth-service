package project.authservice.application.dto;

import lombok.Getter;
import project.authservice.domain.model.Role;
import java.util.Collection;

@Getter
public class UserDto
{
    private final String email;
    private final Collection<Role> roles;

    public UserDto( String email, Collection<Role> roles )
    {
        this.email = email;
        this.roles = roles;
    }

    public static UserDtoBuilder builder()
    {
        return new UserDtoBuilder();
    }

    public static class UserDtoBuilder
    {
        private String email;
        private Collection<Role> roles;

        public UserDtoBuilder email( String email )
        {
            this.email = email;
            return this;
        }

        public UserDtoBuilder roles( Collection<Role> roles )
        {
            this.roles = roles;
            return this;
        }

        public UserDto build()
        {
            return new UserDto( email, roles );
        }
    }
}