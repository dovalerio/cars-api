#!/bin/bash

# Server
export SERVER_PORT=8080
export SPRING_APP_NAME=cars-api

# Database
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=carsdb
export DB_USER=carsuser
export DB_PASSWORD=carspass

# JPA
export JPA_DDL_AUTO=update
export JPA_OPEN_IN_VIEW=false
export JPA_SHOW_SQL=false
export JPA_FORMAT_SQL=true

# Redis
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=
export REDIS_TIMEOUT=2000

# MVC
export MVC_PROBLEM_DETAILS_ENABLED=false

# Logging
export LOG_LEVEL_ROOT=INFO
export LOG_LEVEL_SECURITY=DEBUG
export LOG_LEVEL_HIBERNATE_SQL=DEBUG

# Security
export JWT_SECRET=uma_senha_forte_aqui

# Currency API
export CURRENCY_CACHE_TTL=10
export CURRENCY_API_PRIMARY=https://economia.awesomeapi.com.br/json/last/USD-BRL
export CURRENCY_API_FALLBACK=https://api.frankfurter.app/latest?from=USD&to=BRL

echo "Variaveis configuradas com sucesso."