package com.example.wonderwoman.delivery.entity;

import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum PostStatus {
    NONE("없음"),
    CHATTING("채팅중"),
    IN_PROGRESS("진행중"),
    DONE("완료");

    private static final Map<String, PostStatus> statusMap = Stream.of(values())
            .collect(Collectors.toMap(PostStatus::getStatusName, Function.identity()));

    @JsonValue
    private final String statusName;

    @JsonCreator
    public static PostStatus resolve(String statusName) {
        return Optional.ofNullable(statusMap.get(statusName))
                .orElseThrow(() -> new WonderException(ErrorCode.VALUE_NOT_IN_OPTION));
    }
}