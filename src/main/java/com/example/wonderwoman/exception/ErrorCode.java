package com.example.wonderwoman.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    VALUE_NOT_IN_OPTION(HttpStatus.BAD_REQUEST, "선택지에 없는 값을 사용했습니다.", "선택지에 있는 값을 사용해야 합니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 정보를 찾지 못했습니다.", "email 과 password 를 올바르게 입력했는지 확인해주세요"),
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "email 또는 비밀번호가 맞지 않습니다.", "다른 이메일 또는 비밀번호를 사용해야합니다."),
    FORBIDDEN_ARTICLE(HttpStatus.FORBIDDEN, "게시글에 수정, 삭제에 대한 권한이 없습니다.", "잘못된 접근입니다. 입력값을 확인해주세요."),
    ALREADY_MEMBER(HttpStatus.CONFLICT, "이미 존재하는 유저 정보입니다.", "다른 이메일 혹은 닉네임을 사용해야합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료됐거나 권한이 없습니다.", "토큰을 재발급 받아야합니다."),
    VALUE_IS_NONNULL(HttpStatus.BAD_REQUEST, "값을 반드시 넣어야 합니다.", "null 값이 허용되지 않으므로 반드시 값을 전달해주세요."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾지 못했습니다.", "존재하는 채팅방인지 확인해주세요."),
    REDIS_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "레디스 오류입니다.", "레디스 연결을 확인해주세요."),
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "글을 찾지 못했습니다.", "존재하는 글인지 확인해주세요."),
    FORBIDDEN_CHATROOM(HttpStatus.FORBIDDEN, "채팅방에 접근 권한이 없습니다.", "채팅방에 참여하는 회원인지 확인해주세요."),
    ARTICLE_CAN_NOT_DELETE(HttpStatus.FORBIDDEN, "이미 진행중인 글입니다.", "어떠한 상태도 없는 글인지 확인해주세요."),
    BUILDING_NOT_MATCH(HttpStatus.BAD_REQUEST, "건물이 학교와 매칭되지 않습니다.", "건물을 올바르게 선택해주세요."),
    INVALID_POST_STATUS_CHANGE(HttpStatus.BAD_REQUEST, "현재 딜리버리 상태에서는 상태 변경이 불가능합니다.", "딜리버리 상태가 '없음'인 경우에만 취소 가능합니다.");

    private final HttpStatus httpStatus;
    private final String message;
    private final String solution;
}