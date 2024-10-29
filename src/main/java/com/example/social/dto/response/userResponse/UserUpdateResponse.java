package com.example.social.dto.response.userResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateResponse {
    private String message;
    private int Id;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
    private String address;
    private String roles;
    private String birthday;
}
