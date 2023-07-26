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
public enum SanitaryType {
    WING("날개형"),
    ABSORB("흡수형"),
    COTTON("순면"),
    ORGANIC("유기농");

    private static final Map<String, SanitaryType> typeMap = Stream.of(values())
            .collect(Collectors.toMap(SanitaryType::getTypeName, Function.identity()));

    @JsonValue
    private final String typeName;

    @JsonCreator
    public static SanitaryType resolve(String typeName) {
        return Optional.ofNullable(typeMap.get(typeName))
                .orElseThrow(() -> new WonderException(ErrorCode.VALUE_NOT_IN_OPTION));
    }
}
