import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
  vus: 10,
  duration: '1m',
};

const BASE_URL = 'https://jijoonghae.duckdns.org';
const ACCESS_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZW1haWwiOiJranNAdGVzdC5jb20iLCJpYXQiOjE3NTM4NTA1MzEsImV4cCI6MTc1OTAzNDUzMX0.51PegLud1QMFDg2RHuMlNbd7piz8-gh2k-YTa0iDlKE';

const HEADERS = {
  'Content-Type': 'application/json',
  'Authorization': ACCESS_TOKEN,
};

function logOnFail(ok, label, res) {
  if (!ok) console.log(`${label} FAIL ->`, res.status, res.body);
}

export default function () {
  // 각 VU/iteration마다 고유 회원정보 (중복 회피)
  const now = Date.now();
  const email = `k6user+${__VU}-${now}@test.com`;
  const nickname = `k6nick_${__VU}_${now}`;

  // 1) 서비스 상태 확인
  group('1) /api/user/health (POST)', () => {
    const res = http.post(`${BASE_URL}/api/user/health`, null, { headers: HEADERS });
    const ok = check(res, { 'health 200': (r) => r.status === 200 });
    logOnFail(ok, 'HEALTH', res);
  });

  // 2) 이메일/닉네임 중복 확인 (가입 전)
  group('2) Dup Check before signup', () => {
    const r1 = http.get(`${BASE_URL}/api/user/isDupEmail?email=${encodeURIComponent(email)}`, { headers: HEADERS });
    const ok1 = check(r1, { 'isDupEmail 200': (r) => r.status === 200 });
    logOnFail(ok1, 'isDupEmail(before)', r1);

    const r2 = http.get(`${BASE_URL}/api/user/isDupNickname?nickname=${encodeURIComponent(nickname)}`, { headers: HEADERS });
    const ok2 = check(r2, { 'isDupNickname 200': (r) => r.status === 200 });
    logOnFail(ok2, 'isDupNickname(before)', r2);
  });

  // 3) 회원가입
  group('3) /api/user/signup (POST)', () => {
    const payload = JSON.stringify({
      name: 'k6 tester',
      nickname: nickname,
      email: email,
      password: 'k6-pass-1234',
      gender: 'MALE', // SignUpRequestDto.Gender
    });
    const res = http.post(`${BASE_URL}/api/user/signup`, payload, { headers: HEADERS });
    const ok = check(res, { 'signup 200': (r) => r.status === 200 });
    logOnFail(ok, 'SIGNUP', res);
  });

  // 4) 이메일로 사용자 조회
  group('4) GET /api/user?email=', () => {
    const res = http.get(`${BASE_URL}/api/user?email=${encodeURIComponent(email)}`, { headers: HEADERS });
    const ok = check(res, { 'getUserByEmail 200': (r) => r.status === 200 });
    logOnFail(ok, 'GET USER BY EMAIL', res);
  });

  // 5) 현재 사용자 정보 (게이트웨이에서 X-User-email 주입 필요)
  group('5) POST /api/user/currentUserInfo', () => {
    const res = http.post(`${BASE_URL}/api/user/currentUserInfo`, null, { headers: HEADERS });
    const ok = check(res, { 'currentUserInfo 200': (r) => r.status === 200 });
    logOnFail(ok, 'CURRENT USER INFO', res);
  });

  // 6) 사용자 상태 조회/수정 (/api/user/stat)
  group('6) /api/user/stat GET/PUT', () => {
    const rGet = http.get(`${BASE_URL}/api/user/stat`, { headers: HEADERS });
    const okG = check(rGet, { 'stat GET 200': (r) => r.status === 200 });
    logOnFail(okG, 'USER STAT GET', rGet);

    const body = JSON.stringify({ expChange: 10 }); // UserStatusRequestDto
    const rPut = http.put(`${BASE_URL}/api/user/stat`, body, { headers: HEADERS });
    const okP = check(rPut, { 'stat PUT 200': (r) => r.status === 200 });
    logOnFail(okP, 'USER STAT PUT', rPut);
  });

  // 7) 출석 (POST/GET year,month)
  group('7) /api/user/attendance POST/GET', () => {
    const rPost = http.post(`${BASE_URL}/api/user/attendance`, null, { headers: HEADERS });
    const okP = check(rPost, { 'attendance POST 200': (r) => r.status === 200 });
    logOnFail(okP, 'ATTENDANCE POST', rPost);

    // 서울 타임존 기준 현재 연/월
    const d = new Date();
    const year = d.getUTCFullYear(); // 게이트웨이/서버 TZ 무관하게 단순 호출용
    const month = d.getUTCMonth() + 1;

    const rGet = http.get(`${BASE_URL}/api/user/attendance?year=${year}&month=${month}`, { headers: HEADERS });
    const okG = check(rGet, { 'attendance GET 200': (r) => r.status === 200 });
    logOnFail(okG, 'ATTENDANCE GET', rGet);
  });

  // 8) 사용자 정보 수정 (PUT /api/user)
  group('8) PUT /api/user (update info)', () => {
    const payload = JSON.stringify({
      nickname: `${nickname}_upd`,
      address: '서울시 강남구 역삼동',
      password: 'k6-pass-1234', // 변경 테스트 겸 포함
      title: '초보 모험가',
      membership: 'VVIP', // UserRequestDto.Membership (enum)
    });
    const res = http.put(`${BASE_URL}/api/user`, payload, { headers: HEADERS });
    const ok = check(res, { 'updateUserInfo 200': (r) => r.status === 200 });
    logOnFail(ok, 'UPDATE USER INFO', res);
  });

  // 9) 전체 사용자+상태 조회 (GET /api/user/status)
  group('9) GET /api/user/status', () => {
    const res = http.get(`${BASE_URL}/api/user/status`, { headers: HEADERS });
    const ok = check(res, { 'users and status 200': (r) => r.status === 200 });
    logOnFail(ok, 'GET ALL USERS & STATUS', res);
  });

  sleep(1);
}
