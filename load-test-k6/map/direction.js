import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '1m',
  thresholds: {
    checks: ['rate>0.95'],
  },
};

const BASE_URL = 'https://jijoonghae.duckdns.org';
const ACCESS_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZW1haWwiOiJranNAdGVzdC5jb20iLCJpYXQiOjE3NTM4NTA1MzEsImV4cCI6MTc1OTAzNDUzMX0.51PegLud1QMFDg2RHuMlNbd7piz8-gh2k-YTa0iDlKE';

// ✅ 요청하신 대로 헤더 구조/값 유지 (Bearer 접두어 추가하지 않음)
const HEADERS = {
  'Content-Type': 'application/json',
  'Authorization': ACCESS_TOKEN,
};

// 공통 유틸
function safeData(res) {
  try {
    const j = res.json();
    return j?.data ?? j;
  } catch (e) {
    return null;
  }
}
function logOnFail(ok, label, res) {
  if (!ok) console.log(`${label} FAIL ->`, res.status, res.body);
}
function pickId(obj) {
  if (!obj || typeof obj !== 'object') return null;
  return obj.id || obj.directionId || obj._id || obj.uuid || obj.directionUUID || null;
}

export default function () {
  let createdDirectionId = null;

  // 예시 PathFindRequestDto (서울 강남역 → 잠실역, 경유지 1개)
  const pathPayload = JSON.stringify({
    origin: {
      name: '강남역',
      x: '127.027621', // 경도 (문자열)
      y: '37.497942', // 위도 (문자열)
      angle: 90,
    },
    destination: {
      name: '잠실역',
      x: '127.100168',
      y: '37.513950',
    },
    waypoints: [
      { name: '선릉역', x: 127.049555, y: 37.504537 },
    ],
    priority: 'RECOMMEND',
    car_fuel: 'GASOLINE',
    car_hipass: false,
    alternatives: false,
    road_details: false,
    summary: true,
  });

  // 1) 자동차 길찾기
  group('1) POST /api/map/direction/path', () => {
    const res = http.post(`${BASE_URL}/api/map/direction/path`, pathPayload, { headers: HEADERS });
    const ok = check(res, { 'path 200': (r) => r.status === 200 });
    logOnFail(ok, 'PATH', res);

    // 생성 응답에서 id 시도
    const data = safeData(res);
    if (data) {
      // data가 객체면 직접, 배열이면 첫 요소에서 id 추출
      createdDirectionId = pickId(Array.isArray(data) ? data[0] : data) || createdDirectionId;
    }
  });

//   // 2) AI 경유지 포함 길찾기
//   group('2) POST /api/map/direction/pathByLLM', () => {
//     const res = http.post(`${BASE_URL}/api/map/direction/pathByLLM`, pathPayload, { headers: HEADERS });
//     const ok = check(res, { 'pathByLLM 200': (r) => r.status === 200 });
//     logOnFail(ok, 'PATH BY LLM', res);
//   });

  // 3) 나의 길찾기 내역
  group('3) GET /api/map/direction (history)', () => {
    const res = http.get(`${BASE_URL}/api/map/direction`, { headers: HEADERS });
    const ok = check(res, { 'history 200': (r) => r.status === 200 });
    logOnFail(ok, 'HISTORY', res);

    // 생성 id가 없으면 히스토리에서 하나 선택
    if (!createdDirectionId) {
      const data = safeData(res);
      if (Array.isArray(data) && data.length > 0) {
        createdDirectionId = pickId(data[0]);
      }
    }
  });

  // 4) 즐겨찾기된 길찾기 목록
  group('4) GET /api/map/direction/bookmark', () => {
    const res = http.get(`${BASE_URL}/api/map/direction/bookmark`, { headers: HEADERS });
    const ok = check(res, { 'bookmark list 200': (r) => r.status === 200 });
    logOnFail(ok, 'BOOKMARK LIST', res);
  });

  // 5) 특정 길찾기 상세
  group('5) GET /api/map/direction/{id}', () => {
    if (!createdDirectionId) {
      console.log('DETAIL SKIP: no direction id');
      return;
    }
    const res = http.get(`${BASE_URL}/api/map/direction/${encodeURIComponent(createdDirectionId)}`, { headers: HEADERS });
    const ok = check(res, { 'detail 200': (r) => r.status === 200 });
    logOnFail(ok, 'DETAIL', res);
  });

  // 6) 즐겨찾기 토글 (true → false)
  group('6) PUT /api/map/direction?id=...&bookmark=...', () => {
    if (!createdDirectionId) {
      console.log('BOOKMARK UPDATE SKIP: no direction id');
      return;
    }
    // 즐겨찾기 ON
    const resOn = http.put(
      `${BASE_URL}/api/map/direction?id=${encodeURIComponent(createdDirectionId)}&bookmark=true`,
      null,
      { headers: HEADERS }
    );
    const okOn = check(resOn, { 'bookmark ON 200': (r) => r.status === 200 });
    logOnFail(okOn, 'BOOKMARK ON', resOn);

    // 즐겨찾기 OFF
    const resOff = http.put(
      `${BASE_URL}/api/map/direction?id=${encodeURIComponent(createdDirectionId)}&bookmark=false`,
      null,
      { headers: HEADERS }
    );
    const okOff = check(resOff, { 'bookmark OFF 200': (r) => r.status === 200 });
    logOnFail(okOff, 'BOOKMARK OFF', resOff);
  });

  // 7) 삭제
  group('7) DELETE /api/map/direction/{id}', () => {
    if (!createdDirectionId) {
      console.log('DELETE SKIP: no direction id');
      return;
    }
    const res = http.del(`${BASE_URL}/api/map/direction/${encodeURIComponent(createdDirectionId)}`, null, { headers: HEADERS });
    const ok = check(res, { 'delete 200': (r) => r.status === 200 });
    logOnFail(ok, 'DELETE', res);
  });

  sleep(1);
}
