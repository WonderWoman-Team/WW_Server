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
public enum ReqType {
    REQUEST("요청"),
    DISPATCH("충동");

    private static final Map<String, ReqType> statusMap = Stream.of(values())
            .collect(Collectors.toMap(ReqType::getTypeName, Function.identity()));

    @JsonValue
    private final String typeName;

    @JsonCreator
    public static ReqType resolve(String typeName) {
        return Optional.ofNullable(statusMap.get(typeName))
                .orElseThrow(() -> new WonderException(ErrorCode.VALUE_NOT_IN_OPTION));
    }
}
