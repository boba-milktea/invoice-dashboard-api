package edu.hyf.invoice.common.utils;
import edu.hyf.invoice.security.UserPrincipal;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component

public class AccessHelper {

    public UUID resolveOwnerUserId(UserPrincipal userPrincipal, UUID dtoOwnerUserId) {
        if (userPrincipal.isSuperAdmin()) {
            if (dtoOwnerUserId == null) {
                throw new IllegalArgumentException("OwnerUserId is required for super admin");
            }
            return dtoOwnerUserId;
        } else {
            return userPrincipal.getId();
        }
    }
}
