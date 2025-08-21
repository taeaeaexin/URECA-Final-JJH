import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '1m',
//   thresholds: {
//     checks: ['rate>0.95'],
//   },
};

const BASE_URL = 'https://jijoonghae.duckdns.org';
const ACCESS_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZW1haWwiOiJranNAdGVzdC5jb20iLCJpYXQiOjE3NTM4NTA1MzEsImV4cCI6MTc1OTAzNDUzMX0.51PegLud1QMFDg2RHuMlNbd7piz8-gh2k-YTa0iDlKE';

// ✅ 요청하신 대로 헤더 구조/값 유지 (Bearer 접두어 추가하지 않음)
const HEADERS = {
  'Content-Type': 'application/json',
  'Authorization': ACCESS_TOKEN,
};

function logOnFail(ok, label, res) {
  if (!ok) console.log(`${label} FAIL ->`, res.status, res.body);
}
function safeData(res) {
  try {
    const j = res.json();
    return j?.data ?? j;
  } catch (_) {
    return null;
  }
}

// ===== 환경변수 =====
const ENV = {
  STORE_ID: 'd4f311e7-7d18-4dc3-8ef8-5f6e9a5fa309',
  BRAND_ID: '716628c5-6069-11f0-86b7-02ecb0678daf',
  BENEFIT_ID: '36303162-3433-3731-2d36-3738632d3131',
  CATEGORY: '문화시설',
  IMAGE_PATH: '../image/영수증.png',   // 예: -e IMAGE_PATH=./receipt.jpg
};

// ===== 테스트 시작 =====
export default function () {

  // 1) 브랜드 목록 조회 (키워드/정렬)
  group('1) GET /api/map/brand', () => {
    const u1 = `${BASE_URL}/api/map/brand?sortBy=asc`;
    const u2 = `${BASE_URL}/api/map/brand?keyword=${encodeURIComponent('GS')}&sortBy=desc`;

    const res1 = http.get(u1, { headers: HEADERS });
    const ok1 = check(res1, { 'brand asc 200': (r) => r.status === 200 });
    logOnFail(ok1, 'BRAND ASC', res1);

    const res2 = http.get(u2, { headers: HEADERS });
    const ok2 = check(res2, { 'brand keyword desc 200': (r) => r.status === 200 });
    logOnFail(ok2, 'BRAND KW DESC', res2);
  });

  // 2) 제휴처 목록 조회 (지도 영역 + 필터)
  function toQuery(params) {
  return Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null && v !== '') // 빈 값 제외
    .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`)
    .join('&');
    }

  group('2) GET /api/map/store (지도영역/필터)', () => {
    const query = toQuery({
      keyword: 'GS',
      category: ENV.CATEGORY,
      benefit: '',
      latMin: 37.48,
      latMax: 37.52,
      lngMin: 127.00,
      lngMax: 127.10,
      centerLat: 37.50,
      centerLng: 127.03,
    });

    const url = `${BASE_URL}/api/map/store?${query}`;
    const res = http.get(url, { headers: HEADERS });
    const ok = check(res, { 'store list 200': (r) => r.status === 200 });
    logOnFail(ok, 'STORE LIST', res);
  });


  // 3) 제휴처 목록 조회2 (위치 제외)
  group('3) GET /api/map/stores (키워드/카테고리)', () => {
    const u = `${BASE_URL}/api/map/stores?keyword=${encodeURIComponent('GS')}&category=${encodeURIComponent(ENV.CATEGORY)}&benefit=`;
    const res = http.get(u, { headers: HEADERS });
    const ok = check(res, { 'stores list2 200': (r) => r.status === 200 });
    logOnFail(ok, 'STORES LIST2', res);
  });

  // 4) 제휴처 상세
  group('4) GET /api/map/store/{storeId}', () => {
    if (!ENV.STORE_ID) {
      console.log('STORE DETAIL SKIP: set -e STORE_ID=<uuid>');
      return;
    }
    const res = http.get(`${BASE_URL}/api/map/store/${encodeURIComponent(ENV.STORE_ID)}`, { headers: HEADERS });
    const ok = check(res, { 'store detail 200': (r) => r.status === 200 });
    logOnFail(ok, 'STORE DETAIL', res);
  });

  // 5) 브랜드 별 혜택
  group('5) GET /api/map/benefit/{brandId}', () => {
    if (!ENV.BRAND_ID) {
      console.log('BENEFITS SKIP: set -e BRAND_ID=<uuid>');
      return;
    }
    const res = http.get(`${BASE_URL}/api/map/benefit/${encodeURIComponent(ENV.BRAND_ID)}`, { headers: HEADERS });
    const ok = check(res, { 'benefit by brand 200': (r) => r.status === 200 });
    logOnFail(ok, 'BENEFIT BY BRAND', res);
  });

  // 6) 내 혜택 사용 내역 (게이트웨이가 X-User-email 주입해야 함)
  group('6) GET /api/map/usage?storeId=(optional)', () => {
    const url = ENV.STORE_ID
      ? `${BASE_URL}/api/map/usage?storeId=${encodeURIComponent(ENV.STORE_ID)}`
      : `${BASE_URL}/api/map/usage`;
    const res = http.get(url, { headers: HEADERS });
    const ok = check(res, { 'my usage 200': (r) => r.status === 200 });
    logOnFail(ok, 'MY USAGE', res);
  });

//   // 7) OCR 업로드 (멀티파트) -> OCR 파싱
//   group('7) POST /api/map/ocr (multipart)', () => {
//     if (!ENV.IMAGE_PATH) {
//       console.log('OCR SKIP: set -e IMAGE_PATH=./receipt.jpg');
//       return;
//     }
//     // k6에서 multipart/form-data 전송: http.file 사용
//     const payload = {
//       imageFile: http.file(open(ENV.IMAGE_PATH, 'b'), 'receipt.jpg'),
//     };
//     const headers = { ...HEADERS }; // Content-Type 자동설정 위해 삭제하지 않음 (k6가 boundary 포함 설정)
//     delete headers['Content-Type'];
//     const res = http.post(`${BASE_URL}/api/map/ocr`, payload, { headers });
//     const ok = check(res, { 'ocr 200': (r) => r.status === 200 });
//     logOnFail(ok, 'OCR', res);
//   });

  // 8) OCR 저장 (구조화 결과 저장) - 실제 사용시 7) 응답 바디를 가공/전달
  group('8) POST /api/map/ocr/save (OCR 결과 저장)', () => {
    // 데모용 더미 페이로드 (실서비스에선 7) 응답의 dto 그대로 사용 권장)
    const dummyDto = JSON.stringify({
    storeName: "GS25 역삼점",
    partnerBrand: "GS25",
    category: "편의점",
    address: "서울 강남구 역삼동 123-45",
    visitedAt: "2025-08-05T17:30:00", // 반드시 이 형식
    totalAmount: 4500,
    benefitAmount: 500,
    });
    const res = http.post(`${BASE_URL}/api/map/ocr/save`, dummyDto, { headers: HEADERS });
    const ok = check(res, { 'ocr save 200': (r) => r.status === 200 });
    logOnFail(ok, 'OCR SAVE', res);
  });

  // 9) distinct 카테고리
  group('9) GET /api/map/distinctCategory', () => {
    const res = http.get(`${BASE_URL}/api/map/distinctCategory`, { headers: HEADERS });
    const ok = check(res, { 'distinct category 200': (r) => r.status === 200 });
    logOnFail(ok, 'DISTINCT CATEGORY', res);
  });

  // 10) 카테고리로 브랜드 목록
  group('10) GET /api/map/brandByCategory?category=...', () => {
    const url = `${BASE_URL}/api/map/brandByCategory?category=${encodeURIComponent(ENV.CATEGORY)}`;
    const res = http.get(url, { headers: HEADERS });
    const ok = check(res, { 'brand by category 200': (r) => r.status === 200 });
    logOnFail(ok, 'BRAND BY CATEGORY', res);
  });

  // 11) 브랜드명 by id
  group('11) GET /api/map/brandNameById?id=...', () => {
    if (!ENV.BRAND_ID) {
      console.log('BRAND NAME BY ID SKIP: set -e BRAND_ID=<uuid>');
      return;
    }
    const res = http.get(`${BASE_URL}/api/map/brandNameById?id=${encodeURIComponent(ENV.BRAND_ID)}`, { headers: HEADERS });
    const ok = check(res, { 'brand name by id 200': (r) => r.status === 200 });
    logOnFail(ok, 'BRAND NAME BY ID', res);
  });

  // 12) 혜택명 by id
  group('12) GET /api/map/benefitNameById?id=...', () => {
    if (!ENV.BENEFIT_ID) {
      console.log('BENEFIT NAME BY ID SKIP: set -e BENEFIT_ID=<uuid>');
      return;
    }
    const res = http.get(`${BASE_URL}/api/map/benefitNameById?id=${encodeURIComponent(ENV.BENEFIT_ID)}`, { headers: HEADERS });
    const ok = check(res, { 'benefit name by id 200': (r) => r.status === 200 });
    logOnFail(ok, 'BENEFIT NAME BY ID', res);
  });

  // 13) 제휴처 랭킹
  group('13) GET /api/map/store/rank', () => {
    const res = http.get(`${BASE_URL}/api/map/store/rank`, { headers: HEADERS });
    const ok = check(res, { 'store rank 200': (r) => r.status === 200 });
    logOnFail(ok, 'STORE RANK', res);
  });

  // 14) 사용자 랭킹
  group('14) GET /api/map/user/rank', () => {
    const res = http.get(`${BASE_URL}/api/map/user/rank`, { headers: HEADERS });
    const ok = check(res, { 'user rank 200': (r) => r.status === 200 });
    logOnFail(ok, 'USER RANK', res);
  });

  sleep(1);
}
