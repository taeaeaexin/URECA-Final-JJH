import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '1m',
};

const BASE_URL = 'https://jijoonghae.duckdns.org';
const ACCESS_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZW1haWwiOiJranNAdGVzdC5jb20iLCJpYXQiOjE3NTM4NTA1MzEsImV4cCI6MTc1OTAzNDUzMX0.51PegLud1QMFDg2RHuMlNbd7piz8-gh2k-YTa0iDlKE';

const HEADERS = {
  'Content-Type': 'application/json',
  'Authorization': ACCESS_TOKEN, // ✅ 요청대로 Bearer 미첨부, 구조 유지
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
  let allMissions = [];
  let myMissionsAll = [];
  let myMissionsIncomplete = [];
  let myMissionsComplete = [];

  // 1) 모든 미션 목록 조회
  group('1) GET /api/user/mission (모든 미션)', () => {
    const res = http.get(`${BASE_URL}/api/user/mission`, { headers: HEADERS });
    const ok = check(res, { 'all missions 200': (r) => r.status === 200 });
    logOnFail(ok, 'ALL MISSIONS', res);
    const data = safeData(res);
    if (Array.isArray(data)) allMissions = data;
  });

  // 2) 내 미션 목록 조회 - 전체
  group('2) GET /api/user/mission/my (전체)', () => {
    const res = http.get(`${BASE_URL}/api/user/mission/my`, { headers: HEADERS });
    const ok = check(res, { 'my missions (all) 200': (r) => r.status === 200 });
    logOnFail(ok, 'MY MISSIONS ALL', res);
    const data = safeData(res);
    if (Array.isArray(data)) myMissionsAll = data;
  });

  // 3) 내 미션 목록 조회 - 미완료만
  group('3) GET /api/user/mission/my?completed=false (미완료)', () => {
    const res = http.get(`${BASE_URL}/api/user/mission/my?completed=false`, { headers: HEADERS });
    const ok = check(res, { 'my missions (incomplete) 200': (r) => r.status === 200 });
    logOnFail(ok, 'MY MISSIONS INCOMPLETE', res);
    const data = safeData(res);
    if (Array.isArray(data)) myMissionsIncomplete = data;
  });

  // 4) 내 미션 목록 조회 - 완료만
  group('4) GET /api/user/mission/my?completed=true (완료)', () => {
    const res = http.get(`${BASE_URL}/api/user/mission/my?completed=true`, { headers: HEADERS });
    const ok = check(res, { 'my missions (complete) 200': (r) => r.status === 200 });
    logOnFail(ok, 'MY MISSIONS COMPLETE', res);
    const data = safeData(res);
    if (Array.isArray(data)) myMissionsComplete = data;
  });

  // 5) 미션 완료
  group('5) GET /api/user/mission/complete?missionId=... (미션 완료)', () => {
    // 우선 미완료 미션 -> 없으면 전체/내 전체에서 하나 선택
    const candidate =
      (myMissionsIncomplete && myMissionsIncomplete[0]) ||
      (myMissionsAll && myMissionsAll.find(() => true)) ||
      (allMissions && allMissions[0]);

    if (!candidate) {
      console.log('COMPLETE SKIP: no mission candidate available');
      return;
    }

    const missionId = candidate.missionId || candidate.id || candidate.uuid || candidate.missionUUID;
    if (!missionId) {
      console.log('COMPLETE SKIP: candidate has no identifiable missionId', JSON.stringify(candidate));
      return;
    }

    const url = `${BASE_URL}/api/user/mission/complete?missionId=${encodeURIComponent(missionId)}`;
    const res = http.get(url, { headers: HEADERS });
    const ok = check(res, { 'mission complete 200': (r) => r.status === 200 });
    logOnFail(ok, 'MISSION COMPLETE', res);
  });

  sleep(1);
}
