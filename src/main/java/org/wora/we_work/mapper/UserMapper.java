package org.wora.we_work.mapper;

import org.mapstruct.Mapper;
import org.wora.we_work.dto.UserDTO;
import org.wora.we_work.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
}
