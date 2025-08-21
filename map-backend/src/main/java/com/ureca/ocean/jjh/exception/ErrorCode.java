package com.ureca.ocean.jjh.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //auth_backend : 10000
    LOGIN_FAIL(10000,"LOGIN_FAIL","LOGIN에 실패했습니다."),
    //user_backend : 20000
    NOT_FOUND_USER(20001,"NOT_FOUND_USER","해당 사용자가 없습니다."),
    USER_ALREADY_EXIST(20002,"USER_ALREADY_EXIST","해당 사용자가 이미 있습니다."),

    //map_backend : 30000
    NOT_FOUND_ERROR(30000,"NOT_FOUND_ERROR","조회된 데이터가 없습니다."),
    BOOKMARD_ALREADY_EXIST(30001,"BOOKMARD_ALREADY_EXIST","즐겨찾기가 이미 있습니다."),
    OCR_PROCESSING_FAIL(30002, "OCR_PROCESSING_FAIL","OCR 처리에 실패했습니다."),
    OCR_NO_RESULT(30003, "OCR_NO_RESULT", "OCR 결과가 없습니다."),
    OCR_GEMINI_PARSE_FAIL(30004, "OCR_GEMINI_PARSE_FAIL", "OCR PARSING에 실패했습니다."),
    USER_ID_PARSING_ERROR(30005, "USER_ID_PARSING_ERROR", "USER ID 파싱에 실패했습니다."),
    STORE_USAGES_IS_EMPTY(30006, "STORE_USAGES_IS_EMPTY", "STORE USAGES가 비어있습니다."),
    NOT_FOUND_BRAND(30007,"NOT_FOUND_BRAND","해당 BRAND가 없습니다."),
    NOT_FOUND_BENEFIT(30008,"NOT_FOUND_BENEFIT","해당 혜택이 없습니다."),
    PARSING_MCP_RESPONSE_ERROR(30009,"PARSING_MCP_RESPONSE_ERROR","MCP의 답변을 PARSING하는 중에 오류가 났습니다."),
    ALREADY_SAVED_BILL(30009, "ALREADY_SAVED_BILL","이미 저장한 영수증입니다."),
    ;
	
    //ai_backend : 40000
    private final int code;
    private final String name;
    private final String message;
}