package project.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuthServiceApplicationTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordMatch() {
        String rawPassword = "Senha@123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("As senhas correspondem? " + matches);

        assertTrue(matches);
    }
}