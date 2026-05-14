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


}
