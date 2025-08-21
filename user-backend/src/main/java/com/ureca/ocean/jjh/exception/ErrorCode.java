package com.ureca.ocean.jjh.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //auth_backend : 10000
    //user_backend : 20000
    NOT_FOUND_USER(20001,"NOT_FOUND_USER","해당 사용자가 없습니다."),
    USER_ALREADY_EXIST(20002,"USER_ALREADY_EXIST","해당 사용자가 이미 있습니다."),
    USER_STATUS_SAVE_FAIL(20003,"USER_STATUS_SAVE_FAIL","사용자 STATUS를 저장하는 중 오류가 발생"),
    USER_STATUS_NOT_EXIST(20004,"USER_STATUS_NOT_EXIST","해당 사용자 STATUS가 존재하지 않습니다."),
    ATTENDANCE_ALREADY_DONE(20005, "ATTENDANCE_ALREADY_DONE", "이미 출석 체크 하였습니다."),
    ENUM_NOT_PARSED(20006,"ENUM_NOT_PARSED","ENUM을 parsing할 때 오류가 발생하였습니다."),
    POST_NOT_FOUND(20007,"POST_NOT_FOUND","POST를 찾을 수 없습니다."),
    NOT_FOUND_USER1(20008, "NOT_FOUND_USER1","completed 파라미터가 null입니다. true 또는 false를 지정해주세요."),
    NOT_FOUND_USER2(20009, "NOT_FOUND_USER2","completed 값은 null일 수 없습니다."),
    NOT_FOUND_CHATROOM(20010, "NOT_FOUND_CHATROOM","채팅방을 찾을 수 없습니다."),
    NOT_AUTHORIZED(20011,"NOT_AUTHORIZED","글을 삭제할 권한이 없습니다."),
    ATTENDANCE_REQUIRE(200012, "ATTENDANCE_REQUIRE","출석체크가 필요합니다."),
    MISSION_NOT_COMPLETED(200013, "MISSION_NOT_COMPLETED","완료되지 않은 미션입니다."),
    NOT_FOUND_MISSION(20014, "NOT_FOUND_MISSION","미션을 찾을 수 없습니다."),
    ALREADY_COMPLETED(20015, "ALREADY_COMPLETED","이미 완료된 미션입니다."),
    JUSO_CONVERT_FAIL(20016, "JUSO_CONVERT_FAIL","주소 변환중 오류 발생"),
    ;

    //map_backend : 30000

    //ai_backend : 40000
    private final int code;
    private final String name;
    private final String message;
}
