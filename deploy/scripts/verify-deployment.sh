#!/usr/bin/env bash
set -Eeuo pipefail

container="${1:-kolog_backend}"
timeout_seconds="${2:-180}"
probe_url="${3:-http://127.0.0.1:8080/api/v1/logs/hours}"
deadline=$((SECONDS + timeout_seconds))

while (( SECONDS < deadline )); do
  if ! docker inspect "$container" >/dev/null 2>&1; then
    echo "Container $container does not exist." >&2
    exit 1
  fi

  status="$(docker inspect --format '{{.State.Status}}' "$container")"
  health="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}missing{{end}}' "$container")"
  restarts="$(docker inspect --format '{{.RestartCount}}' "$container")"

  case "$status" in
    exited|dead|removing)
      echo "Container $container entered terminal state: $status" >&2
      exit 1
      ;;
  esac

  if (( restarts > 0 )); then
    echo "Container $container restarted $restarts time(s); refusing a restart loop." >&2
    exit 1
  fi

  if [[ "$status" == "running" && "$health" == "healthy" ]]; then
    docker exec "$container" curl --fail --silent --show-error "$probe_url" >/dev/null
    printf 'DEPLOYMENT_HEALTHY container=%s status=%s health=%s restarts=%s\n' \
      "$container" "$status" "$health" "$restarts"
    exit 0
  fi

  sleep 5
done

docker inspect --format 'status={{.State.Status}} health={{if .State.Health}}{{.State.Health.Status}}{{else}}missing{{end}} restarts={{.RestartCount}} exit={{.State.ExitCode}}' "$container" >&2 || true
echo "Container $container did not become healthy within ${timeout_seconds}s." >&2
exit 1
