import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '1m',
};

const BASE_URL = 'https://jijoonghae.duckdns.org';
const ACCESS_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZW1haWwiOiJranNAdGVzdC5jb20iLCJpYXQiOjE3NTM4NTA1MzEsImV4cCI6MTc1OTAzNDUzMX0.51PegLud1QMFDg2RHuMlNbd7piz8-gh2k-YTa0iDlKE';

// ✅ 요청하신 대로 헤더 구조/값 유지 (Bearer 접두어 추가 안 함)
const HEADERS = {
  'Content-Type': 'application/json',
  'Authorization': ACCESS_TOKEN,
};

// BaseResponseDto 안전 파서
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

export default function () {
  let initialBookmarks = [];
  let addedStoreId = null;
  const ENV_STORE_ID = __ENV.STORE_ID; // 등록/삭제 테스트용

  // 1) 즐겨찾기 목록 조회 (전체)
  group('1) GET /api/map/bookmark (전체)', () => {
    const res = http.get(`${BASE_URL}/api/map/bookmark`, { headers: HEADERS });
    const ok = check(res, { 'bookmark list (all) 200': (r) => r.status === 200 });
    logOnFail(ok, 'BOOKMARK LIST ALL', res);
    const data = safeData(res);
    if (Array.isArray(data)) initialBookmarks = data;
  });

  // 2) 즐겨찾기 목록 조회 (카테고리 필터: 예시 두 개)
  group('2) GET /api/map/bookmark?category=편의점&category=카페', () => {
    const url = `${BASE_URL}/api/map/bookmark?category=${encodeURIComponent('편의점')}&category=${encodeURIComponent('카페')}`;
    const res = http.get(url, { headers: HEADERS });
    const ok = check(res, { 'bookmark list (filtered) 200': (r) => r.status === 200 });
    logOnFail(ok, 'BOOKMARK LIST FILTERED', res);
  });

  // 3) 즐겨찾기 등록 (환경변수 STORE_ID 필요)
  group('3) POST /api/map/bookmark/{storeId} (등록)', () => {
    const STORE_ID = '3fa85f64-5717-4562-b3fc-2c963f66afa6';
    const res = http.post(`${BASE_URL}/api/map/bookmark/${encodeURIComponent(STORE_ID)}`, null, { headers: HEADERS });
    const ok = check(res, { 'bookmark add 200': (r) => r.status === 200 });
    logOnFail(ok, 'BOOKMARK ADD', res);
    if (ok) addedStoreId = STORE_ID;
  });

  // 4) 즐겨찾기 등록 검증 (옵션)
  group('4) GET /api/map/bookmark (등록 검증)', () => {
    if (!addedStoreId) {
      console.log('VERIFY ADD SKIP: no addedStoreId');
      return;
    }
    const res = http.get(`${BASE_URL}/api/map/bookmark`, { headers: HEADERS });
    const ok = check(res, { 'bookmark list after add 200': (r) => r.status === 200 });
    logOnFail(ok, 'BOOKMARK LIST AFTER ADD', res);
    if (ok) {
      const data = safeData(res) || [];
      const exists = data.some(s => (s.storeId || s.id || s.uuid) === addedStoreId);
      if (!exists) console.log('WARNING: added store not found in list:', addedStoreId);
    }
  });

  // 5) 즐겨찾기 삭제
  group('5) DELETE /api/map/bookmark/{storeId} (삭제)', () => {
    if (!addedStoreId) {
      console.log('DELETE SKIP: no addedStoreId');
      return;
    }
    const res = http.del(`${BASE_URL}/api/map/bookmark/${encodeURIComponent(addedStoreId)}`, null, { headers: HEADERS });
    const ok = check(res, { 'bookmark delete 200': (r) => r.status === 200 });
    logOnFail(ok, 'BOOKMARK DELETE', res);
  });

  // 6) 즐겨찾기 삭제 검증 (옵션)
  group('6) GET /api/map/bookmark (삭제 검증)', () => {
    if (!addedStoreId) {
      console.log('VERIFY DELETE SKIP: no addedStoreId');
      return;
    }
    const res = http.get(`${BASE_URL}/api/map/bookmark`, { headers: HEADERS });
    const ok = check(res, { 'bookmark list after delete 200': (r) => r.status === 200 });
    logOnFail(ok, 'BOOKMARK LIST AFTER DELETE', res);
    if (ok) {
      const data = safeData(res) || [];
      const exists = data.some(s => (s.storeId || s.id || s.uuid) === addedStoreId);
      if (exists) console.log('WARNING: deleted store still present in list:', addedStoreId);
    }
  });

  sleep(1);
}
