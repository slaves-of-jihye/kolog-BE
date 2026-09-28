#!/usr/bin/env bash
# build/run/health check 스테이지 중 하나라도 실패했을 때 post.failure에서 호출된다.
# kolog-be:latest를 덮어쓰기 직전에 남겨둔 kolog-be:rollback으로 되돌리고
# 그 이미지로 컨테이너를 다시 띄운다. 되돌릴 이미지가 없으면(최초 배포 실패) 조용히 끝낸다.
set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_DIR="$(dirname "$SCRIPT_DIR")"
COMPOSE_FILE="$DEPLOY_DIR/build/docker-compose.yml"
ENV_FILE="$DEPLOY_DIR/env/.env"

echo "[rollback] 배포 실패 감지. 이전 이미지로 롤백을 시도합니다."

if ! docker image inspect kolog-be:rollback >/dev/null 2>&1; then
    echo "[rollback] kolog-be:rollback 이미지가 없습니다 (최초 배포였거나 백업 시점이 없음). 롤백을 건너뜁니다."
    exit 0
fi

docker tag kolog-be:rollback kolog-be:latest
echo "[rollback] kolog-be:latest를 kolog-be:rollback 시점으로 되돌렸습니다."

if ! docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d; then
    echo "[rollback] 롤백 이미지로도 컨테이너 기동에 실패했습니다. 수동 확인이 필요합니다."
    exit 1
fi

echo "[rollback] 이전에 실행 중이던 앱으로 롤백 완료."
