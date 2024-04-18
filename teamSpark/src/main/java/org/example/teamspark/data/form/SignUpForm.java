package org.example.teamspark.data.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.validation.ValidPassword;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignUpForm {
    @NotBlank(message = "Name may not be blank")
    private String name;
    @NotBlank(message = "Email may not be blank")
    @Email(message = "Invalid email format. Please provide a valid email address.")
    private String email;
    @NotBlank(message = "Password may not be blank")
    @ValidPassword
    private String password;
}
