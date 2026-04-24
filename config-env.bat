@echo off

REM Server
set SERVER_PORT=8080
set SPRING_APP_NAME=cars-api

REM Database
set DB_HOST=10.0.0.28
set DB_PORT=5432
set DB_NAME=carsdb
set DB_USER=carsuser
set DB_PASSWORD=carspass

REM JPA
set JPA_DDL_AUTO=update
set JPA_OPEN_IN_VIEW=false
set JPA_SHOW_SQL=false
set JPA_FORMAT_SQL=true

REM Redis
set REDIS_HOST=10.0.0.28
set REDIS_PORT=6379
set REDIS_PASSWORD=
set REDIS_TIMEOUT=2000

REM MVC
set MVC_PROBLEM_DETAILS_ENABLED=false

REM Logging
set LOG_LEVEL_ROOT=INFO
set LOG_LEVEL_SECURITY=DEBUG
set LOG_LEVEL_HIBERNATE_SQL=DEBUG

REM Security
set JWT_SECRET=cars-api-super-secret-key-with-at-least-32-characters-123456
set JWT_EXPIRATION=3600000

REM Currency API
set CURRENCY_CACHE_TTL=10
set CURRENCY_API_PRIMARY=https://economia.awesomeapi.com.br/json/last/USD-BRL
set CURRENCY_API_FALLBACK=https://api.frankfurter.app/latest?from=USD^&to=BRL

echo Variaveis configuradas com sucesso.