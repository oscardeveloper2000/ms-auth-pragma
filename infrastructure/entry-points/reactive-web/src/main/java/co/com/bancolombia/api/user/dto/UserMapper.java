package co.com.bancolombia.api.user.dto;

import org.mapstruct.Mapper;
import co.com.bancolombia.model.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toModel(UserRecord userDTO);
    UserRecord toDTO(User user);
}
