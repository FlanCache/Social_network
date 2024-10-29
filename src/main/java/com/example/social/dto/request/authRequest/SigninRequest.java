package com.example.social.dto.request.authRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SigninRequest {

    @NotBlank(message = "Email must not be null")
    @Email
    private String email;

    @NotBlank(message = "Password must not be null")
    @Length(min = 5,max = 10,message = "Password must between 5 - 10 characters")
    private String password;
}
