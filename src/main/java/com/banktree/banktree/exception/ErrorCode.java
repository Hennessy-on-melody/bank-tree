package com.banktree.banktree.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ALREADY_EXIST_USER("이미 존재하는 사용자입니다."),
    BAD_REQUEST("잘못된 접근입니다."),
    USER_NOT_FOUNDED("존재하지 않는 사용자 입니다."),
    WRONG_PASSWORD("올바르지 않은 비밀번호 입니다."),
    UNAUTHORIZED_USER("인증되지 않은 회원입니다."),
    NO_TOKEN_INFORMATION("토큰 권한이 없습니다.");

    private final String getErrorMessage;

}
