package com.banktree.banktree.exception;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UsersException extends RuntimeException{
    private ErrorCode errorCode;
    private String errorMessage;

    public UsersException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getGetErrorMessage();
    }
}
