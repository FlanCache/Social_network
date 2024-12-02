package com.example.social.dto.response.userResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSigninResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String otp;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userFullName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userEmail;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userPhone;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userAvatar;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userAddress;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userBirthday;
}
