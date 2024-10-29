package com.example.social.dto.request.authRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(type = "object" ,example = "{\n" +
        "  \"email\": \"string\",\n" +
        "  \"password\": \"string\",\n" +
        "  \"fullName\": \"string\"\n" +
        "}")
public class SigupRequest {
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^\\S+@\\S+\\.\\S+$",message = "Invalid email")
    private String Email;

    @NotBlank(message = "Password is required")
    @Length(min = 5,max = 10,message = "Long between 5 - 10 characters")
    private String password;

    @NotBlank(message = "Name can not be empty")
    private String fullName;
}
