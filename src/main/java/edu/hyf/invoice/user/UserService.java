package edu.hyf.invoice.user;

import edu.hyf.invoice.auth.dto.RegisterRequestDTO;
import edu.hyf.invoice.common.exception.EmailAlreadyExistsException;
import edu.hyf.invoice.common.exception.UserNotFoundByIdException;
import edu.hyf.invoice.security.JwtService;
import edu.hyf.invoice.user.dto.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    //TODO - to make it clear that a user is registered
    @Transactional
    public void register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(registerRequestDTO.getEmail());
        }

        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword())); // hashed pw
        user.setEmail(registerRequestDTO.getEmail());
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    public UserResponse findUserById(UUID id) {
        return userMapper.toResponseDTO(userRepository.findById(id).orElseThrow(() -> new UserNotFoundByIdException(id)));
    }

}
