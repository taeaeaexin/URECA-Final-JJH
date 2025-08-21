package com.ureca.ocean.jjh.common.exception;

import com.ureca.ocean.jjh.common.BaseResponseDto;
import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.ureca.ocean.jjh")
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(UserException.class)
    public BaseResponseDto<ErrorResponseDto> handleUserException(UserException e){
        log.error(e.getMessage(),e);
        return BaseResponseDto.fail(e.getErrorCode());
    }

    // JSON 파싱 오류 (예: 잘못된 enum 값 포함)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseResponseDto<ErrorResponseDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("JSON parse error: ", e);
        return BaseResponseDto.fail(ErrorCode.ENUM_NOT_PARSED);
    }
}
