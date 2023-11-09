package com.banktree.banktree.domain.form;


import com.banktree.banktree.domain.entity.AccountType;
import lombok.*;


public class CreateAccountForm {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateAccountReq {
        private String userEmail;
        private String accountNickname;
        private AccountType accountType;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateAccountRes{
        private String userEmail;
        private String accountNickname;
        private AccountType accountType;
        private String message;
        private Long accountNumber;
        public CreateAccountRes from(CreateAccountReq req){
            return CreateAccountRes.builder()
                    .userEmail(req.getUserEmail())
                    .accountNickname(null)
                    .accountType(req.getAccountType())
                    .accountNumber(this.accountNumber)
                    .message(this.message)
                    .build();

        }
    }
}
