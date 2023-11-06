package com.banktree.banktree.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ALREADY_EXIST_USER("이미 존재하는 사용자입니다.");


    private final String getErrorMessage;

}
