package com.example.wonderwoman.delivery.response;

import com.example.wonderwoman.common.dto.NormalResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeliveryPostResponseDto extends NormalResponseDto {
    private String postTitle;
    private String reqType;
    private int postNumber;
    private String sanitarySize;
    private String sanitaryType;
    private String postStatus;

    public DeliveryPostResponseDto(String postTitle, String reqType, int postNumber,
                                   String sanitarySize, String sanitaryType, String postStatus) {
        super("SUCCESS");
        this.postTitle = postTitle;
        this.reqType = reqType;
        this.postNumber = postNumber;
        this.sanitarySize = sanitarySize;
        this.sanitaryType = sanitaryType;
        this.postStatus = postStatus;
    }

    public static DeliveryPostResponseDto success(String postTitle, String reqType, int postNumber,
                                                  String sanitarySize, String sanitaryType, String postStatus) {
        return new DeliveryPostResponseDto(postTitle, reqType, postNumber, sanitarySize, sanitaryType, postStatus);
    }
}
