package com.ureca.ocean.jjh.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class MapException extends RuntimeException {
    private final ErrorCode errorCode;

    public MapException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}