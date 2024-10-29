package com.example.social.dto.request.userRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @Email(message = "Email format invalid")
    @NotNull(message = "Email cannot be null")
    private String email;

    @NotBlank(message = " New password invalid, new password cannot be empty")
    @NotEmpty(message = "New password can not be empty")
    @NotNull(message = " New password invalid, new password cannot be NULL")
    private String newPassword;

    @NotNull(message = "Otp can not be null")
    @NotEmpty(message = "Otp can not be empty")
    @NotBlank(message = "Otp can not be empty")
    private String otp;
}
