package com.ureca.ocean.jjh.user.client;

import com.ureca.ocean.jjh.user.dto.UserAndMembershipDto;
import com.ureca.ocean.jjh.user.dto.UserAndStatusResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ureca.ocean.jjh.common.BaseResponseDto;
import com.ureca.ocean.jjh.common.constant.DomainConstant;
import com.ureca.ocean.jjh.user.dto.UserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClient {
	
    private final RestTemplate restTemplate;
    
    public UserDto getUserByEmail(String email) {
        String url = DomainConstant.USER_URL + "api/user?email="+ email ;
        System.out.println("url : "+ url);
        ResponseEntity<BaseResponseDto<UserDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseResponseDto<UserDto>>() {}
        );
        UserDto userDto = response.getBody().getData();
        log.info("userDto 받은 거 : " + userDto.getPassword() );
        return userDto;
    }

    public UserAndMembershipDto getUserAndMembershipByEmail(String email) {
        String url = DomainConstant.USER_URL + "api/user?email="+ email ;
        System.out.println("url : "+ url);
        ResponseEntity<BaseResponseDto<UserAndMembershipDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseResponseDto<UserAndMembershipDto>>() {}
        );
        UserAndMembershipDto userAndMembershipDto = response.getBody().getData();
        log.info("userDto 받은 거 : " + userAndMembershipDto.getPassword() );
        return userAndMembershipDto;
    }

    public List<UserAndStatusResponseDto> getAllUserAndStatus() {
        String url = DomainConstant.USER_URL + "api/user/status";
        System.out.println("url : "+ url);
        ResponseEntity<BaseResponseDto<List<UserAndStatusResponseDto>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseResponseDto<List<UserAndStatusResponseDto>>>() {}
        );
        List<UserAndStatusResponseDto> result = response.getBody().getData();
        log.info("userDto 받은 거 : " + result);
        return result;
    }
}