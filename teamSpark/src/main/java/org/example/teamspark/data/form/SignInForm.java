package org.example.teamspark.data.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignInForm {
    @NotBlank(message = "Email may not be blank")
    private String email;
    @NotBlank(message = "Password may not be blank")
    private String password;
}


