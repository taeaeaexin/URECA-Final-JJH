package com.ureca.ocean.jjh.aibackend.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ureca.ocean.jjh.aibackend.client.dto.UserDto;
import com.ureca.ocean.jjh.aibackend.common.BaseResponseDto;
import com.ureca.ocean.jjh.aibackend.common.constant.DomainConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClient {
	
    private final RestTemplate restTemplate;
    
    public UserDto getUserByEmail(String email) {
        String url = DomainConstant.USER_URL + "api/user?email=" + email ;
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
    
}
