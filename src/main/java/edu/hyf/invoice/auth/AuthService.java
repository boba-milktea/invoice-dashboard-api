package edu.hyf.invoice.auth;

import edu.hyf.invoice.auth.dto.AuthResponseDTO;
import edu.hyf.invoice.auth.dto.LoginRequestDTO;
import edu.hyf.invoice.common.exception.UserNotFoundByEmailException;
import edu.hyf.invoice.security.JwtService;
import edu.hyf.invoice.user.User;
import edu.hyf.invoice.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponseDTO login (LoginRequestDTO loginRequestDTO) {

            User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                    .orElseThrow(() -> new UserNotFoundByEmailException(loginRequestDTO.getEmail()));

            if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
                throw new UserNotFoundByEmailException(loginRequestDTO.getEmail());
            }

            String token =  jwtService.generateToken(user);

            AuthResponseDTO authResponseDTO = new AuthResponseDTO();
            authResponseDTO.setToken(token);
            authResponseDTO.setName(user.getName());
            authResponseDTO.setRole(user.getRole());

            return authResponseDTO;
        }
    }

