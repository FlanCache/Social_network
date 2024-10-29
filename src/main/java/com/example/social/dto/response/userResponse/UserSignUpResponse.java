package com.example.social.dto.response.userResponse;

import com.example.social.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpResponse {
    private String message;
    private String userEmail;
}
