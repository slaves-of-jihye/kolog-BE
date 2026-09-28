#!/usr/bin/env bash
# kolog-be-app 컨테이너가 docker-compose.yml에 정의된 healthcheck 기준으로
# "healthy"가 될 때까지 폴링한다. 제한 시간 안에 healthy가 되지 않으면 실패로 종료한다.
set -uo pipefail

CONTAINER_NAME="${1:-kolog-be-app}"
TIMEOUT_SECONDS="${2:-90}"
INTERVAL_SECONDS=3
elapsed=0

echo "[health-check] $CONTAINER_NAME 컨테이너 상태 확인 시작 (최대 ${TIMEOUT_SECONDS}s)"

while [ "$elapsed" -lt "$TIMEOUT_SECONDS" ]; do
    status="$(docker inspect --format '{{.State.Health.Status}}' "$CONTAINER_NAME" 2>/dev/null || echo "missing")"

    if [ "$status" = "healthy" ]; then
        echo "[health-check] $CONTAINER_NAME healthy (경과 ${elapsed}s)"
        exit 0
    fi

    if [ "$status" = "missing" ]; then
        echo "[health-check] $CONTAINER_NAME 컨테이너를 찾을 수 없습니다."
        exit 1
    fi

    if [ "$status" = "unhealthy" ]; then
        echo "[health-check] $CONTAINER_NAME unhealthy로 판정됨."
        docker logs "$CONTAINER_NAME" --tail 50 || true
        exit 1
    fi

    sleep "$INTERVAL_SECONDS"
    elapsed=$((elapsed + INTERVAL_SECONDS))
done

echo "[health-check] ${TIMEOUT_SECONDS}s 동안 healthy 상태가 되지 않았습니다 (마지막 상태: $status)."
docker logs "$CONTAINER_NAME" --tail 50 || true
exit 1
