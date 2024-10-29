package com.example.social.dto.request.userRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(type = "object" ,example = "{\n" +
        "  \"fullName\": \"string\",\n" +
        "  \"phone\": \"string\",\n" +
        "  \"address\": \"string\",\n" +
        "  \"birthday\": \"dd/mm/yyyy\"\n" +
        "}")
public class UserUpdateRequest {
    private String fullName;
    private String phone;
    private String address;
    @Pattern(regexp = "^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[0-2])/((19|20)\\d\\d)$",message = "Invalid date")
    private String birthday;

}
