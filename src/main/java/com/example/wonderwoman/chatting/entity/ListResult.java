package com.example.wonderwoman.chatting.entity;

import com.example.wonderwoman.common.dto.NormalResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListResult<T> extends NormalResponseDto {
    private List<T> list;
}
