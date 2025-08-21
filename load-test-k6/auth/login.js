import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 5 },   // 30초 동안 VU를 5명까지 증가
    { duration: '1m', target: 10 },   // 1분 동안 10명 유지
    { duration: '30s', target: 0 },   // 30초 동안 0명으로 감소 (정리 단계)
  ],
};

const BASE_URL = 'https://jijoonghae.duckdns.org'; // 실제 서버 주소
const LOGIN_ENDPOINT = '/api/auth/login';

export default function () {
  const payload = JSON.stringify({
    email: 'karina@gmail.com',
    password: 'karina',
  });

  const headers = {
    'Content-Type': 'application/json',
  };

  const res = http.post(`${BASE_URL}${LOGIN_ENDPOINT}`, payload, { headers });

  check(res, {
    '✅ 로그인 성공 (200)': (r) => r.status === 200,
    '✅ accessToken 포함': (r) =>
      r.json('data.token') !== undefined,
  });

  sleep(1); // 사용자 간 요청 간격
}
