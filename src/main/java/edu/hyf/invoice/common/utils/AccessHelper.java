package edu.hyf.invoice.common.utils;
import edu.hyf.invoice.common.exception.UserNotFoundByIdException;
import edu.hyf.invoice.security.UserPrincipal;
import edu.hyf.invoice.user.User;
import edu.hyf.invoice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor

public class AccessHelper {
    private final UserRepository userRepository;

    public UUID resolveOwnerUserId(UserPrincipal userPrincipal, UUID dtoOwnerUserId) {

        if (userPrincipal.isSuperAdmin()) {

            if (dtoOwnerUserId == null) {

                throw new IllegalArgumentException("OwnerUserId is required for super admin");
            }

            User user = userRepository.findById(dtoOwnerUserId).orElseThrow(() -> new UserNotFoundByIdException(dtoOwnerUserId));

            return user.getId();

        } else {

            return userPrincipal.getId();
        }
    }
}
