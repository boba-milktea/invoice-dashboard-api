package edu.hyf.invoice.user;

import edu.hyf.invoice.auth.dto.RegisterRequestDTO;
import edu.hyf.invoice.common.exception.EmailAlreadyExistsException;
import edu.hyf.invoice.common.exception.UserNotFoundByIdException;
import edu.hyf.invoice.user.dto.UserPatchRequest;
import edu.hyf.invoice.user.dto.UserResponse;
import edu.hyf.invoice.user.dto.UserRolePatchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public  UserResponse register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(registerRequestDTO.getEmail());
        }

        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword())); // hashed pw
        user.setEmail(registerRequestDTO.getEmail());
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    public UserResponse findUserById(UUID id) {
        return userMapper.toResponseDTO(userRepository.findById(id).orElseThrow(()
                -> new UserNotFoundByIdException(id)));
    }

    @Transactional
    public UserResponse updateUserById(UUID id, UserPatchRequest dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundByIdException(id));
        userMapper.updateUser(dto, user);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Transactional
    public UserResponse updateUserRole(UUID id, UserRolePatchRequest dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundByIdException(id));
        userMapper.updateUserRole(dto, user);
        return userMapper.toResponseDTO(user);
    }

    @Transactional
    public void deleteUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundByIdException(id));
        userRepository.delete(user);
    }

}
