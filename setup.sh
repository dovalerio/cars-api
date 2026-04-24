#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

import_env_file() {
  local file="$1"
  while IFS= read -r line || [ -n "$line" ]; do
    case "$line" in
      ''|'#'*) continue ;;
    esac

    local key="${line%%=*}"
    local value="${line#*=}"
    export "$key=$value"
  done < "$file"
}

if [ -f ".env" ]; then
  import_env_file ".env"
elif [ -f ".env.example" ]; then
  import_env_file ".env.example"
fi

export SERVER_PORT="${SERVER_PORT:-8080}"
export SPRING_APP_NAME="${SPRING_APP_NAME:-cars-api}"

export DB_HOST="${DB_HOST:-localhost}"
export DB_PORT="${DB_PORT:-5432}"
export DB_NAME="${DB_NAME:-carsdb}"
export DB_USER="${DB_USER:-carsuser}"
export DB_PASSWORD="${DB_PASSWORD:-carspass}"

export REDIS_HOST="${REDIS_HOST:-localhost}"
export REDIS_PORT="${REDIS_PORT:-6379}"
export REDIS_PASSWORD="${REDIS_PASSWORD:-}"
export REDIS_TIMEOUT="${REDIS_TIMEOUT:-2000}"

export JWT_SECRET="${JWT_SECRET:-cars-api-super-secret-key-with-at-least-32-characters-123456}"

export CURRENCY_CACHE_TTL="${CURRENCY_CACHE_TTL:-10}"
export CURRENCY_API_PRIMARY="${CURRENCY_API_PRIMARY:-https://economia.awesomeapi.com.br/json/last/USD-BRL}"
export CURRENCY_API_FALLBACK="${CURRENCY_API_FALLBACK:-https://api.frankfurter.app/latest?from=USD&to=BRL}"

export LOG_LEVEL_ROOT="${LOG_LEVEL_ROOT:-INFO}"
export LOG_LEVEL_SECURITY="${LOG_LEVEL_SECURITY:-DEBUG}"
export LOG_LEVEL_HIBERNATE_SQL="${LOG_LEVEL_HIBERNATE_SQL:-DEBUG}"

export JPA_DDL_AUTO="${JPA_DDL_AUTO:-update}"
export JPA_OPEN_IN_VIEW="${JPA_OPEN_IN_VIEW:-false}"
export JPA_SHOW_SQL="${JPA_SHOW_SQL:-false}"
export JPA_FORMAT_SQL="${JPA_FORMAT_SQL:-true}"

export MVC_PROBLEM_DETAILS_ENABLED="${MVC_PROBLEM_DETAILS_ENABLED:-false}"

docker compose up -d

mvn spring-boot:run