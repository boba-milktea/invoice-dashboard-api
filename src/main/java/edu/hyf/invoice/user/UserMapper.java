package edu.hyf.invoice.user;

import edu.hyf.invoice.auth.dto.RegisterRequestDTO;
import edu.hyf.invoice.user.dto.UserPatchRequest;
import edu.hyf.invoice.user.dto.UserResponse;
import edu.hyf.invoice.user.dto.UserRolePatchRequest;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)

//TODO remove mapstruct
public interface UserMapper {
    UserResponse toResponseDTO (User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target= "password", ignore = true)
    @Mapping(target = "clients", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(RegisterRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target= "password", ignore = true)
    @Mapping(target = "clients", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateUser(UserPatchRequest dto, @MappingTarget User user);

    default void updateUserRole(UserRolePatchRequest request, User user) {

        if (request.role() == null ) throw new IllegalArgumentException("Empty role is not allowed");

        if (request.role() == Role.SUPER_ADMIN) throw new IllegalArgumentException("SUPER_ADMIN role cannot be assigned");

        if (request.role() != Role.USER && request.role() != Role.ADMIN) throw new IllegalArgumentException("Invalid User Role");

        if (request.role() == Role.ADMIN) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

    }

    /*
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "clients", ignore = true)
    @Mapping(target= "password", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUserRole(UserRolePatchRequest dto, @MappingTarget User user);
     */


}
