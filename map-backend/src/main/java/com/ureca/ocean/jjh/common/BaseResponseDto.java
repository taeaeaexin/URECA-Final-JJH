package com.ureca.ocean.jjh.common;


import com.ureca.ocean.jjh.common.exception.ErrorResponseDto;
import com.ureca.ocean.jjh.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponseDto<T> {

    private Integer statusCode;
    private String message;
    private T data;

    public static <T> BaseResponseDto<T> success(T data) {
        return BaseResponseDto.<T>builder()
                .statusCode(200)
                .message("success")
                .data(data)
                .build();
    }

    public static BaseResponseDto<ErrorResponseDto> fail(ErrorCode errorCode) {
        //service layer -> global exception handler -> this method ( controller를 거치지 않음 )
        //Errorcode에 있는 정보로 ErrorResponseDto를 만든다.
        return BaseResponseDto.<ErrorResponseDto>builder()
                .statusCode(errorCode.getCode())
                .message("fail")
                .data(ErrorResponseDto.of(errorCode))
                .build();
    }
}