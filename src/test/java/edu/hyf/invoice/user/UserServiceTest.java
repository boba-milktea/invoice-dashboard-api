package edu.hyf.invoice.user;

import edu.hyf.invoice.auth.dto.RegisterRequestDTO;
import edu.hyf.invoice.common.exception.EmailAlreadyExistsException;
import edu.hyf.invoice.common.exception.UserNotFoundByIdException;
import edu.hyf.invoice.security.UserPrincipal;
import edu.hyf.invoice.user.dto.UserPatchRequest;
import edu.hyf.invoice.user.dto.UserResponse;
import edu.hyf.invoice.user.dto.UserRolePatchRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class UserServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserPrincipal userPrincipal;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;




    @Test
    void findAll_shouldReturnAllUsers() {

        User user = new User();
        UserResponse response = mock(UserResponse.class);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(response);

        List<UserResponse> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(userRepository).findAll();
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void findUserById_shouldReturnUser() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        UserResponse response = mock(UserResponse.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(response);

       UserResponse result = userService.findUserById(userId);

        assertEquals(response, result);

        verify(userRepository).findById(userId);
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void updateUser_shouldChangeEmail_withNewEmailProvided() {

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail("oldEmail@hyf.com");

        UserPatchRequest request = new UserPatchRequest(
                null,
                "newEmail@hyf.com"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        doAnswer(invocation -> {
            UserPatchRequest dto = invocation.getArgument(0);
            User u = invocation.getArgument(1);
            if (dto.email() != null) {
                u.setEmail(dto.email());
            }
            return null;
        }).when(userMapper).updateUser(eq(request), eq(user));

        UserResponse response = mock(UserResponse.class);


        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(response);

        UserResponse result = userService.updateUserById(userId, request);

        assertEquals(response, result);
        assertEquals("newEmail@hyf.com", user.getEmail());

        verify(userRepository).findById(userId);
        verify(userMapper).updateUser(request, user);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void updateUserById_shouldThrowException_withUserNotFound(){

        UUID userId = UUID.randomUUID();

        UserPatchRequest request = new UserPatchRequest(
                "test",
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundByIdException exception = assertThrows(
                UserNotFoundByIdException.class,
                () -> userService.updateUserById(userId, request)
        );

        assertEquals("User not found with id: " + userId, exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(userMapper, never()).updateUser(any(), any());
    }


    @Test
    void updateUserRoleById_shouldUpdateUserRole_whenTheUserIsSuperAdmin() {

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setRole(Role.USER);

        UserRolePatchRequest request = new UserRolePatchRequest(
                Role.ADMIN
        );

        UserResponse response = mock(UserResponse.class);

        when(userPrincipal.isSuperAdmin()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(userPrincipal.getId()).thenReturn(UUID.randomUUID());

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(response);

        UserResponse result = userService.updateUserRoleById(userId, request, userPrincipal);

        assertEquals(Role.ADMIN, user.getRole());
        assertEquals(response, result);

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void updateUserRoleById_shouldThrowException_whenTheUserIsNotSuperAdmin() {

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setRole(Role.ADMIN);

        UserRolePatchRequest request = new UserRolePatchRequest(
                Role.SUPER_ADMIN
        );


        when(userPrincipal.isSuperAdmin()).thenReturn(false);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> userService.updateUserRoleById(userId, request, userPrincipal)
        );

        assertEquals("Access Denied.", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(userRepository, never()).findById(any());
    }


    @Test
    void updateUserRoleById_shouldThrowException_whenSuperAdminChangesOwnRole() {

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setRole(Role.ADMIN);

        UserRolePatchRequest request = new UserRolePatchRequest(
                Role.USER
        );

        when(userPrincipal.isSuperAdmin()).thenReturn(true);
        when(userPrincipal.getId()).thenReturn(userId);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUserRoleById(userId, request, userPrincipal)
        );

        assertEquals("Changing self's role is not allowed.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserRoleById_shouldThrowException_whenSuperAdminChangesRole() {

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setRole(Role.SUPER_ADMIN);

        UserRolePatchRequest request = new UserRolePatchRequest(
                Role.USER
        );

        when(userPrincipal.isSuperAdmin()).thenReturn(true);
        when(userPrincipal.getId()).thenReturn(UUID.randomUUID());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUserRoleById(userId, request, userPrincipal)
        );

        assertEquals("Cannot downgrade a Super Admin.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }


    @Test
    void updateUserRoleById_shouldThrowException_whenTheRoleIsNull() {

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setRole(Role.USER);

        UserRolePatchRequest request = new UserRolePatchRequest(
                null
        );

        when(userPrincipal.isSuperAdmin()).thenReturn(true);
        when(userPrincipal.getId()).thenReturn(UUID.randomUUID());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUserRoleById(userId, request, userPrincipal)
        );

        assertEquals("Empty role is not allowed", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserRoleById_shouldThrowException_whenAssignRoleAsSuperAdmin() {

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setRole(Role.USER);

        UserRolePatchRequest request = new UserRolePatchRequest(
                Role.SUPER_ADMIN
        );

        when(userPrincipal.isSuperAdmin()).thenReturn(true);
        when(userPrincipal.getId()).thenReturn(UUID.randomUUID());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUserRoleById(userId, request, userPrincipal)
        );

        assertEquals("SUPER_ADMIN role cannot be assigned", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserRoleById_shouldThrowException_whenUserIsNotFound() {

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setRole(Role.USER);

        UserRolePatchRequest request = new UserRolePatchRequest(
                Role.ADMIN
        );

        when(userPrincipal.isSuperAdmin()).thenReturn(true);
        when(userPrincipal.getId()).thenReturn(UUID.randomUUID());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundByIdException exception = assertThrows(
                UserNotFoundByIdException.class,
                () -> userService.updateUserRoleById(userId, request, userPrincipal)
        );

        assertEquals("User not found with id: " + userId, exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUserById_shouldDeleteUser_whenTheUserIsNotSuperAdmin() {

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setRole(Role.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }


    @Test
    void deleteUserById_shouldThrowException_whenTheUserIsSuperAdmin() {

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setRole(Role.SUPER_ADMIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteUserById(userId)
        );

        assertEquals("Removing a Super Admin is not allowed.", exception.getMessage());

        verify(userRepository, never()).delete(any());
    }

    @Test
    void register_shouldRegisterUser_whenTheRequestIsValid() {

        String password = "JAVA_is_fun";

        RegisterRequestDTO request = new RegisterRequestDTO(
                "mocktest",
                "mocktest@hyf.com",
                password
        );

        UserResponse response = mock(UserResponse.class);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(response);

        userService.register(request);

        verify(passwordEncoder).encode(password);
        verify(userRepository).save(argThat(u ->
                u.getEmail().equals(request.getEmail()) && u.getRole() == Role.USER));
    }

    @Test
    void register_shouldThrowException_whenTheEmailExists() {

        String existedEmail = "mockTest1@hyf.com";

        RegisterRequestDTO request = new RegisterRequestDTO(
                "mocktest",
                "mockTest1@hyf.com",
                "mockTest"
        );
        when(userRepository.existsByEmail("mockTest1@hyf.com")).thenReturn(true);


        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.register(request)
        );

        assertEquals("Email: " + existedEmail + " already exists.", exception.getMessage());

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void register_shouldReturnEncodedPassword() {

        String password = "I_LIKE_coding";

        RegisterRequestDTO request = new RegisterRequestDTO(
                "mocktest",
                "mocktest@hyf.com",
                password
        );

        UserResponse response = mock(UserResponse.class);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(response);

        UserResponse result = userService.register(request);

        assertEquals(result, response);

        verify(userRepository).save(argThat(u -> "hashed".equals(u.getPassword())));

    }

}

/*
### Tests

- [x] Get all users — `findAll_shouldReturnAllUsers`
- [x] Get a user by ID — `findUserById_shouldReturnUser`
- [x] User not found — `updateUserById_shouldThrowException_withUserNotFound`, `updateUserRoleById_shouldThrowException_whenUserIsNotFound`
- [ ] User not found (`findUserById`, `deleteUserById`) — not covered yet
- [x] Duplicate email — `register_shouldThrowException_whenTheEmailExists`
- [ ] Password is not exposed in response — not covered (`UserResponse` has no password field; add an explicit mapper/response test if desired)
- [x] Update user profile — `updateUser_shouldChangeEmail_withNewEmailProvided`
- [x] Update user role (happy path + error cases) — `updateUserRoleById_*` (8 tests)
- [x] Delete user — `deleteUserById_shouldDeleteUser_whenTheUserIsNotSuperAdmin`, `deleteUserById_shouldThrowException_whenTheUserIsSuperAdmin`
- [x] Register user — `register_shouldRegisterUser_whenTheRequestIsValid`, `register_shouldReturnEncodedPassword` (password hashed before save)
 */
