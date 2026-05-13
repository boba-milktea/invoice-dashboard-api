package edu.hyf.invoice.user;

import edu.hyf.invoice.security.UserPrincipal;
import edu.hyf.invoice.user.dto.UserPatchRequest;
import edu.hyf.invoice.user.dto.UserResponse;
import edu.hyf.invoice.user.dto.UserRolePatchRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated

/*
GET    /api/v1/users/me          logged-in user
PATCH  /api/v1/users/me          logged-in user

GET    /api/v1/users             ADMIN
GET    /api/v1/users/{id}        ADMIN
PATCH  /api/v1/users/{id}        ADMIN
PATCH  /api/v1/users/{id}/role   ADMIN
DELETE /api/v1/users/{id}        ADMIN
 */

//TODO uncomment PreAuthorize
//@PreAuthorize("hasRole('ADMIN')")

public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping("/me")
    public ResponseEntity<@NonNull UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userService.findUserById(userPrincipal.getId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<@NonNull UserResponse> updateCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UserPatchRequest dto) {
        return ResponseEntity.ok(userService.updateUserById(userPrincipal.getId(), dto));
    }

    @GetMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NonNull List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NonNull UserResponse> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PatchMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NonNull UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserPatchRequest dto) {
        return ResponseEntity.ok(userService.updateUserById(id, dto));
    }

    @PatchMapping("/{id}/role")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NonNull UserResponse> updateUserRole(@PathVariable UUID id, @Valid @RequestBody  UserRolePatchRequest dto) {
        return ResponseEntity.ok(userService.updateUserRole(id, dto));
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NonNull Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }


}
