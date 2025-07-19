package project.authservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class TokenResponse
{
    private String accessToken;
    private String refreshToken;

    public TokenResponse(String token) {
    }
}