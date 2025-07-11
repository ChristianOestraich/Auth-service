package project.authservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import project.authservice.application.dto.UserDto;
import project.authservice.infrastructure.config.FeignConfig;

@FeignClient( name = "user-service",
              url = "${user-service.url:http://localhost:8081}",
              configuration = FeignConfig.class )
@Component
public interface UserServiceClient
{
    @PostMapping( "/api/users" )
    void createUser( @RequestBody UserDto userDto );
}