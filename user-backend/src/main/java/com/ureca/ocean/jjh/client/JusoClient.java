package com.ureca.ocean.jjh.client;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Component
public class JusoClient {
    private final String apiKey = "U01TX0FVVEgyMDI1MDgwNTE0NDQ1MzExNjAyNjY=";

    // 1. 전체 JSON 응답 문자열 반환
    public String convertJuso(String keywordRaw) throws Exception {
        String keyword = URLEncoder.encode(keywordRaw, "UTF-8");

        String apiURL = "https://business.juso.go.kr/addrlink/addrLinkApi.do"
                + "?confmKey=" + apiKey
                + "&currentPage=1"
                + "&countPerPage=10"
                + "&keyword=" + keyword
                + "&resultType=json";

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();

        System.out.println("응답 결과: " + response.toString());
        return response.toString();
    }

    // '동'만 추출 (emdNm)
    public String extractAddrUpToDong(String jsonStr) throws Exception {
        JSONObject json = new JSONObject(jsonStr);
        JSONArray jusoArray = json.getJSONObject("results").getJSONArray("juso");

        if (jusoArray.length() > 0) {
            JSONObject juso = jusoArray.getJSONObject(0);

            // 각 필드를 조합하여 정확한 행정 주소 구성
            String siNm = juso.getString("siNm");     // 서울특별시
            String sggNm = juso.getString("sggNm");   // 성북구
            String emdNm = juso.getString("emdNm");   // 안암동5가

            // 예: 안암동5가 → 안암동
            String dongOnly = emdNm.replaceAll("(동)[0-9가-힣]*$", "$1"); // ex. 안암동5가 → 안암동

            return siNm + " " + sggNm + " " + dongOnly;
        } else {
            return "검색 결과 없음";
        }
    }

}
