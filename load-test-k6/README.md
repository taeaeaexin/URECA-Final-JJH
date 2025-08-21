# 💥 K6 부하 테스트 환경 구성 및 실행 방법

이 문서는 `K6`와 `Grafana`, `InfluxDB`를 활용한 부하 테스트 환경을 도커(Docker)로 구성하고 실행하는 법을 쉽게 안내합니다.

---

## 📦 1. 도커 환경 준비하기

### 📄 `docker-compose.yml`

```yaml
docker-compose.yml
```

```yaml
version: '3.7'

services:
  influxdb:
    image: influxdb:1.8
    ports:
      - "8086:8086"
    volumes:
      - influxdb-data:/var/lib/influxdb
    environment:
      - INFLUXDB_DB=k6
      - INFLUXDB_HTTP_AUTH_ENABLED=false

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    depends_on:
      - influxdb
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana-data:/var/lib/grafana

volumes:
  influxdb-data:
  grafana-data:
```

### ✅ 실행 방법

```bash
docker-compose up -d
```

* 실행 후:

  * InfluxDB: [http://localhost:8086](http://localhost:8086)
  * Grafana: [http://localhost:3000](http://localhost:3000) (ID: `admin` / PW: `admin`)

---

## 🚀 2. 테스트 스크립트 실행하기

### 📄 `run-k6.bat`

```bat
@echo off
REM 실행할 스크립트 파일명을 인자로 받음
set /p script=" input file path (ex : scripts/test.js): "

docker run -i -v "%cd%:/scripts" grafana/k6 run --out influxdb=http://host.docker.internal:8086/k6 /scripts/%script%
```

### ✅ 실행 방법 (Windows 기준)

```bash
run-k6.bat
```

* 입력 예시:

  ```
   input file path (ex : scripts/test.js): map/store.js
  ```
* 내부적으로 `scripts/map/store.js` 파일을 실행합니다.

---

## 📊 3. Grafana 대시보드 연동하기

1. [http://localhost:3000](http://localhost:3000) 접속
2. InfluxDB 데이터 소스 추가

   * URL: `http://influxdb:8086`
   * Database: `k6`
3. K6용 기본 Dashboard는 템플릿으로 추가하거나 직접 구성 가능합니다.

---

## 🙌 마무리

이 환경은 `부하 테스트`를 실행하고, `시각화`까지 경험해볼 수 있도록 구성되었습니다.
