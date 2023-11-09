package com.banktree.banktree.domain.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerLoginForm {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}

