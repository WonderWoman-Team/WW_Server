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
public enum SanitarySize {
    LINER("라이너"),
    SMALL("소형"),
    MEDIUM("중형"),
    BIG("대형"),
    OVER("오버나이트");

    private static final Map<String, SanitarySize> sizeMap = Stream.of(values())
            .collect(Collectors.toMap(SanitarySize::getSizeName, Function.identity()));

    @JsonValue
    private final String sizeName;

    @JsonCreator
    public static SanitarySize resolve(String sizeName) {
        return Optional.ofNullable(sizeMap.get(sizeName))
                .orElseThrow(() -> new WonderException(ErrorCode.VALUE_NOT_IN_OPTION));
    }
}
