import http from 'k6/http';
import { check, sleep } from 'k6';

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

export default function () {
  // 1. 게시글 작성
  const postPayload = JSON.stringify({
    title: 'GS25 같이 가실 분!',
    content: 'ㅈㄱㄴ',
    category: '편의점',
    brandId: '43a48689-606d-11f0-86b7-02ecb0678daf',
    benefitId: '35663531-3637-3131-2d36-3738632d3131',
    promiseDate: '2025-07-27T14:01:35.299Z',
    storeId: '3fa85f64-5717-4562-b3fc-2c963f66afa6',
  });

  const res1 = http.post(`${BASE_URL}/api/user/article`, postPayload, {
    headers: HEADERS,
  });

  check(res1, {
    '✅ 게시글 작성 성공': (r) => r.status === 200,
  });

  const postId = res1.json('data.postId');

  // 2. 게시글 목록 조회
  const res2 = http.get(`${BASE_URL}/api/user/article?page=0&criteria=createdAt&location=`, {
    headers: HEADERS,
  });

  check(res2, {
    '✅ 게시글 목록 조회 성공': (r) => r.status === 200,
  });

  // 3. 게시글 상세 조회
  if (postId) {
    const res3 = http.get(`${BASE_URL}/api/user/article/detail?postId=${postId}`, {
      headers: HEADERS,
    });

    check(res3, {
    '✅ 게시글 상세 조회 성공': (r) => r.status === 200,
  });
  }

  // 4. 내 게시글 목록 조회
  const res4 = http.get(`${BASE_URL}/api/user/article/myPost?page=0&criteria=createdAt`, {
    headers: HEADERS,
  });

  check(res4, {
    '✅ 내 게시글 조회 성공': (r) => r.status === 200,
  });

  // 5. 게시글 수정
  if (postId) {
    const updatePayload = JSON.stringify({
      title: '수정된 게시글 제목',
      content: '수정된 내용입니다.',
      category: '편의점',
      brandId: '43a48689-606d-11f0-86b7-02ecb0678daf',
      benefitId: '35663531-3637-3131-2d36-3738632d3131',
      promiseDate: new Date().toISOString(),
      location: '서울시 강남구 역삼동',
    });

    const res5 = http.put(`${BASE_URL}/api/user/article?postId=${postId}`, updatePayload, {
      headers: HEADERS,
    });

    check(res5, {
    '✅ 게시글 수정 성공': (r) => r.status === 200,
  });
  }

  // 6. 게시글 삭제
  if (postId) {
    const res6 = http.del(`${BASE_URL}/api/user/article?postId=${postId}`, null, {
      headers: HEADERS,
    });

    check(res6, {
    '✅ 게시글 삭제 성공': (r) => r.status === 200,
  });
  }

  sleep(1);
}
