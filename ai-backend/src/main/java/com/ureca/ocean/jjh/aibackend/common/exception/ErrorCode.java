package com.ureca.ocean.jjh.aibackend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	//auth_backend : 10000
    //user_backend : 20000
//    NOT_FOUND_USER(20001,"NOT_FOUND_USER","해당 사용자가 없습니다."),
    //map_backend : 30000

    //ai_backend : 40000
	SERVER_ERROR(40001, "SERVER_ERROR", "서버 에러"),
	PARSING_ERROR(40002, "PARSING_ERROR", "AI 응답 파싱 에러"),
	CLIENT_CALL_ERROR(40003, "CLIENT_CALL_ERROR", "클라이언트 호출 에러");

    private final int code;
    private final String name;
    private final String message;
}
