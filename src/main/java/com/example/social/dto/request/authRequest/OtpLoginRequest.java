package com.example.social.dto.request.authRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpLoginRequest {
    @NotBlank(message = "Otp cannot be blank")
    @NotNull(message = "Otp cannot be null")
    private String otp;

    @NotBlank(message = "Email cannot be blank")
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email pattern invalid")
    private String email;
}
