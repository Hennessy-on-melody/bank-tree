package com.banktree.banktree.domain.form;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


public class CustomerSignUpForm {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpRequest{
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email
        private String email;
        @NotBlank(message = "이름을 입력해 주세요.")
        private String name;
        @NotBlank(message = "비밀번호를 입력해 주세요.")
        private String password;
        @NotBlank(message = "전화번호를 입력해주세요.")
        private String phoneNumber;

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpResponse{
        @Email
        private String email;
        private String name;

        public static SignUpResponse from(SignUpRequest signUpRequest){
            return SignUpResponse.builder()
                    .email(signUpRequest.email)
                    .name(signUpRequest.name)
                    .build();
        }
    }
}
