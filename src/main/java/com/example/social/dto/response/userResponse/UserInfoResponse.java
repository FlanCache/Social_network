package com.example.social.dto.response.userResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private int id;
    private String email;
    private String fullName;
    private String avatar;
    private String phone;
    private String birthday;
    private String address;
}
