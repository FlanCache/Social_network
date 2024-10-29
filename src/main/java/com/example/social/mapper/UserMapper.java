package com.example.social.mapper;

import com.example.social.dto.PostDto;
import com.example.social.dto.UserDto;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "user.userPassword",target = "userPassword",ignore = true)
    UserDto userToDto(User user);

    @Mapping(target = "userPassword", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User dtoToUser(UserDto userDto);

}
