package com.example.social.mapper;

import com.example.social.dto.UserDto;
import com.example.social.entity.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-01T01:32:42+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto userToDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setUserId( user.getUserId() );
        userDto.setUserFullName( user.getUserFullName() );
        userDto.setUserEmail( user.getUserEmail() );
        userDto.setUserPhone( user.getUserPhone() );
        userDto.setUserAvatar( user.getUserAvatar() );
        userDto.setUserAddress( user.getUserAddress() );
        userDto.setUserRoles( user.getUserRoles() );
        userDto.setUserBirthday( user.getUserBirthday() );
        userDto.setUserOtp( user.getUserOtp() );
        userDto.setUserOtpCreatedTime( user.getUserOtpCreatedTime() );

        return userDto;
    }

    @Override
    public User dtoToUser(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User user = new User();

        user.setUserPassword( userDto.getUserPassword() );
        user.setUserId( userDto.getUserId() );
        user.setUserEmail( userDto.getUserEmail() );
        user.setUserFullName( userDto.getUserFullName() );
        user.setUserAvatar( userDto.getUserAvatar() );
        user.setUserPhone( userDto.getUserPhone() );
        user.setUserOtp( userDto.getUserOtp() );
        user.setUserOtpCreatedTime( userDto.getUserOtpCreatedTime() );
        user.setUserBirthday( userDto.getUserBirthday() );
        user.setUserAddress( userDto.getUserAddress() );
        user.setUserRoles( userDto.getUserRoles() );

        return user;
    }
}
