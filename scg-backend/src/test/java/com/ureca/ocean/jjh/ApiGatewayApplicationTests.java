package com.ureca.ocean.jjh;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApiGatewayApplicationTests {
	//test코드가 모두 통과 했을 때 github actions가 배포를 한다.
	String str1 = "Hello";
	@Test
	void test(){
		assertEquals(str1,"Hello");
	}

}
