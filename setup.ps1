$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

$envFile = Join-Path $root ".env"
$envExampleFile = Join-Path $root ".env.example"

function Import-EnvFile([string]$path) {
    Get-Content $path | ForEach-Object {
        if (-not $_ -or $_.StartsWith("#")) { return }
        $pair = $_ -split "=", 2
        if ($pair.Length -eq 2) {
            [System.Environment]::SetEnvironmentVariable($pair[0], $pair[1], "Process")
        }
    }
}

if (Test-Path $envFile) {
    Import-EnvFile $envFile
} elseif (Test-Path $envExampleFile) {
    Import-EnvFile $envExampleFile
}

$env:SERVER_PORT = if ($env:SERVER_PORT) { $env:SERVER_PORT } else { "8080" }
$env:SPRING_APP_NAME = if ($env:SPRING_APP_NAME) { $env:SPRING_APP_NAME } else { "cars-api" }

$env:DB_HOST = if ($env:DB_HOST) { $env:DB_HOST } else { "localhost" }
$env:DB_PORT = if ($env:DB_PORT) { $env:DB_PORT } else { "5432" }
$env:DB_NAME = if ($env:DB_NAME) { $env:DB_NAME } else { "carsdb" }
$env:DB_USER = if ($env:DB_USER) { $env:DB_USER } else { "carsuser" }
$env:DB_PASSWORD = if ($env:DB_PASSWORD) { $env:DB_PASSWORD } else { "carspass" }

$env:REDIS_HOST = if ($env:REDIS_HOST) { $env:REDIS_HOST } else { "localhost" }
$env:REDIS_PORT = if ($env:REDIS_PORT) { $env:REDIS_PORT } else { "6379" }
$env:REDIS_PASSWORD = if ($env:REDIS_PASSWORD) { $env:REDIS_PASSWORD } else { "" }
$env:REDIS_TIMEOUT = if ($env:REDIS_TIMEOUT) { $env:REDIS_TIMEOUT } else { "2000" }

$env:JWT_SECRET = if ($env:JWT_SECRET) { $env:JWT_SECRET } else { "cars-api-super-secret-key-with-at-least-32-characters-123456" }

$env:CURRENCY_CACHE_TTL = if ($env:CURRENCY_CACHE_TTL) { $env:CURRENCY_CACHE_TTL } else { "10" }
$env:CURRENCY_API_PRIMARY = if ($env:CURRENCY_API_PRIMARY) { $env:CURRENCY_API_PRIMARY } else { "https://economia.awesomeapi.com.br/json/last/USD-BRL" }
$env:CURRENCY_API_FALLBACK = if ($env:CURRENCY_API_FALLBACK) { $env:CURRENCY_API_FALLBACK } else { "https://api.frankfurter.app/latest?from=USD&to=BRL" }

$env:LOG_LEVEL_ROOT = if ($env:LOG_LEVEL_ROOT) { $env:LOG_LEVEL_ROOT } else { "INFO" }
$env:LOG_LEVEL_SECURITY = if ($env:LOG_LEVEL_SECURITY) { $env:LOG_LEVEL_SECURITY } else { "DEBUG" }
$env:LOG_LEVEL_HIBERNATE_SQL = if ($env:LOG_LEVEL_HIBERNATE_SQL) { $env:LOG_LEVEL_HIBERNATE_SQL } else { "DEBUG" }

$env:JPA_DDL_AUTO = if ($env:JPA_DDL_AUTO) { $env:JPA_DDL_AUTO } else { "update" }
$env:JPA_OPEN_IN_VIEW = if ($env:JPA_OPEN_IN_VIEW) { $env:JPA_OPEN_IN_VIEW } else { "false" }
$env:JPA_SHOW_SQL = if ($env:JPA_SHOW_SQL) { $env:JPA_SHOW_SQL } else { "false" }
$env:JPA_FORMAT_SQL = if ($env:JPA_FORMAT_SQL) { $env:JPA_FORMAT_SQL } else { "true" }

$env:MVC_PROBLEM_DETAILS_ENABLED = if ($env:MVC_PROBLEM_DETAILS_ENABLED) { $env:MVC_PROBLEM_DETAILS_ENABLED } else { "false" }

docker compose up -d

mvn spring-boot:run