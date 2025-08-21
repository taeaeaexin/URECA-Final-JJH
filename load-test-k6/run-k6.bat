@echo off
REM 실행할 스크립트 파일명을 인자로 받음
set /p script=" input file path (ex : scripts/test.js): "

docker run -i -v "%cd%:/scripts" grafana/k6 run --out influxdb=http://host.docker.internal:8086/k6 /scripts/%script%
