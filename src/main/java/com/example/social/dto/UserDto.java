package com.example.social.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private int userId;
    private String userFullName;
    private String userPassword;
    private String userEmail;
    private String userPhone;
    private String userAvatar;
    private String userAddress;
    private String userRoles;
    private String userBirthday;
    private String userOtp;
    private LocalDateTime userOtpCreatedTime;
}
