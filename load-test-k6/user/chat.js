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
  // 1. 채팅방 생성
  const createPayload = JSON.stringify({
    postId: '4f2f5a23-42da-496e-a50e-d2b20cfb698a' // 테스트용 postId
  });

  const createRes = http.post(`${BASE_URL}/api/user/chatRoom`, createPayload, {
    headers: HEADERS,
  });

  check(createRes, {
    '채팅방 생성 성공': (res) => res.status === 200,
    '채팅방 ID 반환': (res) => res.json('data.chatRoomId') !== undefined,
  });

  const chatRoomId = createRes.json('data.chatRoomId');

  // 2. 메시지 목록 조회
  if (chatRoomId) {
    const messagesRes = http.get(`${BASE_URL}/api/user/chatRoom/messages?chatRoomId=${chatRoomId}`, {
      headers: HEADERS,
    });

    check(messagesRes, {
      '메시지 조회 성공': (res) => res.status === 200,
    });
  }

  // 3. 내 채팅방 목록 조회
  const listRes = http.get(`${BASE_URL}/api/user/chatRoom`, {
    headers: HEADERS,
  });

  check(listRes, {
    '내 채팅방 목록 조회 성공': (res) => res.status === 200,
  });

  sleep(1);
}
