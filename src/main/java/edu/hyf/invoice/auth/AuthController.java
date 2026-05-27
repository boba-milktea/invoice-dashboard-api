package edu.hyf.invoice.auth;

import edu.hyf.invoice.auth.dto.AuthResponseDTO;
import edu.hyf.invoice.auth.dto.LoginRequestDTO;
import edu.hyf.invoice.auth.dto.RegisterRequestDTO;
import edu.hyf.invoice.user.UserService;
import edu.hyf.invoice.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.security.core.Authentication;
=======
>>>>>>> 1c82202 (add invoice testing)
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

@Validated

public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user.")
    public ResponseEntity<@NonNull UserResponse> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(registerRequestDTO));
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    public ResponseEntity<@NonNull AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }
<<<<<<< HEAD
/*
    @PostMapping("/token")

    public String generateToken (@RequestBody LoginRequestDTO loginRequestDTO){

    }


    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

*/




    /*
    POST /api/auth/register → public
POST /api/auth/login    → public
Swagger/OpenAPI routes  → public
All other endpoints     → authenticated
     */


}


/*
security/
├─ JwtService.java
├─ JwtAuthFilter.java
├─ CustomUserDetailsService.java
└─ UserPrincipal.java

auth/
├─ AuthController.java
├─ AuthService.java
└─ dto/
   ├─ RegisterRequest.java
   ├─ LoginRequest.java
   └─ AuthResponse.java
 */
=======
}
>>>>>>> 1c82202 (add invoice testing)
