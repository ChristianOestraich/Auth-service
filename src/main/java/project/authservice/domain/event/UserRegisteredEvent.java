package project.authservice.domain.event;

import lombok.Getter;
import project.authservice.domain.model.User;

import java.time.Instant;

@Getter
public class UserRegisteredEvent
{
    private final User user;
    private final Instant timestamp;

    public UserRegisteredEvent( User user )
    {
        this.user = user;
        this.timestamp = Instant.now();
    }

    @Override
    public String toString()
    {
        return "UserRegisteredEvent{" +
                "user=" + user.getEmail() +
                ", timestamp=" + timestamp +
                '}';
    }
}
