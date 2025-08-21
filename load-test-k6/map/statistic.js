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

// ✅ 요청대로 헤더 구조/값 유지 (Bearer 접두어 추가하지 않음)
const HEADERS = {
  'Content-Type': 'application/json',
  'Authorization': ACCESS_TOKEN,
};

// 허용되는 Period (enum)
const PERIODS = ['7D', '30D', '180D', '365D', 'ALL'];

// 공통 유틸
function logOnFail(ok, label, res) {
  if (!ok) console.log(`${label} FAIL ->`, res.status, res.body);
}

function runPeriodEndpoint(path, label) {
  group(label, () => {
    for (const p of PERIODS) {
      const url = `${BASE_URL}${path}?period=${p}`;
      const res = http.get(url, { headers: HEADERS });
      const ok = check(res, { [`${label} ${p} 200`]: (r) => r.status === 200 });
      logOnFail(ok, `${label}(${p})`, res);
    }
  });
}

export default function () {
  // 1) 누적/절약 통계
  group('1) GET /api/map/stat/savings', () => {
    const res = http.get(`${BASE_URL}/api/map/stat/savings`, { headers: HEADERS });
    const ok = check(res, { 'savings 200': (r) => r.status === 200 });
    logOnFail(ok, 'SAVINGS', res);
  });

  // 2) 시계열 (일/주/월)
  group('2) GET /api/map/stat/daily?num=10', () => {
    const res = http.get(`${BASE_URL}/api/map/stat/daily?num=10`, { headers: HEADERS });
    const ok = check(res, { 'daily 200': (r) => r.status === 200 });
    logOnFail(ok, 'DAILY', res);
  });

  group('3) GET /api/map/stat/weekly?num=10', () => {
    const res = http.get(`${BASE_URL}/api/map/stat/weekly?num=10`, { headers: HEADERS });
    const ok = check(res, { 'weekly 200': (r) => r.status === 200 });
    logOnFail(ok, 'WEEKLY', res);
  });

  group('4) GET /api/map/stat/monthly?num=5', () => {
    const res = http.get(`${BASE_URL}/api/map/stat/monthly?num=5`, { headers: HEADERS });
    const ok = check(res, { 'monthly 200': (r) => r.status === 200 });
    logOnFail(ok, 'MONTHLY', res);
  });

  // 3) 분포/집계 (Period 필요)
  runPeriodEndpoint('/api/map/stat/category', '5) category');
  runPeriodEndpoint('/api/map/stat/region',  '6) region');
  runPeriodEndpoint('/api/map/stat/weekday', '7) weekday');
  runPeriodEndpoint('/api/map/stat/hourly',  '8) hourly');
  runPeriodEndpoint('/api/map/stat/brand',   '9) brand');
  runPeriodEndpoint('/api/map/stat/store',   '10) store');

  sleep(1);
}
