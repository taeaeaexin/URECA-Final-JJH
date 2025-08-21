package com.ureca.ocean.jjh.aibackend.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ureca.ocean.jjh.aibackend.common.BaseResponseDto;

@RestControllerAdvice("com.ureca.ocean.jjh")
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AiException.class)
    public BaseResponseDto<ErrorResponseDto> handleUserException(AiException e){
        log.error(e.getMessage(),e);
        return BaseResponseDto.fail(e.getErrorCode());
    }

}
