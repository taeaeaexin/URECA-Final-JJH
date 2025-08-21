package com.ureca.ocean.jjh.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException{
    private final ErrorCode errorCode;
}
