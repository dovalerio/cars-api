@echo off
setlocal

cd /d "%~dp0"

if exist .env (
  for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
    if not "%%A"=="" if not "%%A:~0,1"=="#" set "%%A=%%B"
  )
) else if exist .env.example (
  for /f "usebackq tokens=1,* delims==" %%A in (".env.example") do (
    if not "%%A"=="" if not "%%A:~0,1"=="#" set "%%A=%%B"
  )
)

if "%SERVER_PORT%"=="" set SERVER_PORT=8080
if "%SPRING_APP_NAME%"=="" set SPRING_APP_NAME=cars-api

if "%DB_HOST%"=="" set DB_HOST=localhost
if "%DB_PORT%"=="" set DB_PORT=5432
if "%DB_NAME%"=="" set DB_NAME=carsdb
if "%DB_USER%"=="" set DB_USER=carsuser
if "%DB_PASSWORD%"=="" set DB_PASSWORD=carspass

if "%REDIS_HOST%"=="" set REDIS_HOST=localhost
if "%REDIS_PORT%"=="" set REDIS_PORT=6379
if "%REDIS_PASSWORD%"=="" set REDIS_PASSWORD=
if "%REDIS_TIMEOUT%"=="" set REDIS_TIMEOUT=2000

if "%JWT_SECRET%"=="" set JWT_SECRET=cars-api-super-secret-key-with-at-least-32-characters-123456

if "%CURRENCY_CACHE_TTL%"=="" set CURRENCY_CACHE_TTL=10
if "%CURRENCY_API_PRIMARY%"=="" set CURRENCY_API_PRIMARY=https://economia.awesomeapi.com.br/json/last/USD-BRL
if "%CURRENCY_API_FALLBACK%"=="" set CURRENCY_API_FALLBACK=https://api.frankfurter.app/latest?from=USD^&to=BRL

if "%LOG_LEVEL_ROOT%"=="" set LOG_LEVEL_ROOT=INFO
if "%LOG_LEVEL_SECURITY%"=="" set LOG_LEVEL_SECURITY=DEBUG
if "%LOG_LEVEL_HIBERNATE_SQL%"=="" set LOG_LEVEL_HIBERNATE_SQL=DEBUG

if "%JPA_DDL_AUTO%"=="" set JPA_DDL_AUTO=update
if "%JPA_OPEN_IN_VIEW%"=="" set JPA_OPEN_IN_VIEW=false
if "%JPA_SHOW_SQL%"=="" set JPA_SHOW_SQL=false
if "%JPA_FORMAT_SQL%"=="" set JPA_FORMAT_SQL=true

if "%MVC_PROBLEM_DETAILS_ENABLED%"=="" set MVC_PROBLEM_DETAILS_ENABLED=false

docker compose up -d
if errorlevel 1 exit /b 1

mvn spring-boot:run