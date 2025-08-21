package com.ureca.ocean.jjh.aibackend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AiException extends RuntimeException{
	private final ErrorCode errorCode;
}
