package com.example.wonderwoman.chatting.service;

import com.example.wonderwoman.chatting.entity.ListResult;
import com.example.wonderwoman.common.dto.NormalResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {

    public <T> ListResult<T> getListResult(List<T> list) {
        ListResult<T> result = new ListResult<>();
        result.setList(list);
        setSuccessResult(result);
        return result;
    }

    private void setSuccessResult(NormalResponseDto responseDto) {
        responseDto.setStatus("SUCCESS");
    }

    public void setFailResult(NormalResponseDto responseDto) {
        responseDto.setStatus("FAIL");
    }
}
