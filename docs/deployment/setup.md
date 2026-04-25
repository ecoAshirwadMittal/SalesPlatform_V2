# Local Development Setup

## Prerequisites

| Tool | Required Version | Verify |
|------|-----------------|--------|
| Java (OpenJDK) | 21+ | `java -version` |
| Maven | 3.9+ | `mvn --version` |
| Node.js | 24+ | `node --version` |
| npm | 11+ | `npm --version` |
| PostgreSQL | 15+ | `psql --version` |

> Docker is **optional** — only needed if you prefer containerised Postgres/pgAdmin instead of a local install.

## 1. Database Setup

PostgreSQL must be running on `localhost:5432`.

### Option A: Local PostgreSQL (recommended)

Run the bootstrap script as the `postgres` superuser:

```bash
# Windows
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -f bootstrap.sql

# Linux/Mac
psql -U postgres -f bootstrap.sql
```

This creates:
- **Role**: `salesplatform` (password: `salesplatform`)
- **Database**: `salesplatform_dev` (owner: `salesplatform`)

### Option B: Docker Compose

```bash
docker compose up -d postgres
# Optional: pgAdmin at http://localhost:5050 (admin@ecoatm.com / admin)
docker compose up -d pgadmin
```

## 2. Backend (Spring Boot)

```bash
cd backend

# Run Flyway migrations + start the app (port 8080)
mvn spring-boot:run
```

Flyway auto-runs on startup and manages schemas: `identity`, `user_mgmt`, `buyer_mgmt`, `sso`, `pws`, `mdm`, `integration`.

### Verify

```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP","components":{"db":{"status":"UP",...}}}
```

### Spring Security Note

Dev mode generates a random password on each startup (printed in console). The security config will be replaced with proper auth as the project progresses.

## 3. Frontend (Next.js)

```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start dev server (port 3000)
npm run dev
```

### Verify

Open http://localhost:3000 in a browser — you should see the landing page.

## Quick Start (both services)

From the project root (`SalesPlatform_Modern/`):

```bash
# Terminal 1 — Backend
cd backend && mvn spring-boot:run

# Terminal 2 — Frontend
cd frontend && npm run dev
```

## Ports Summary

| Service | Port | URL |
|---------|------|-----|
| PostgreSQL | 5432 | `jdbc:postgresql://localhost:5432/salesplatform_dev` |
| Spring Boot API | 8080 | http://localhost:8080 |
| Actuator health | 8080 | http://localhost:8080/actuator/health |
| Next.js frontend | 3000 | http://localhost:3000 |
| pgAdmin (Docker only) | 5050 | http://localhost:5050 |

## Database Credentials (Dev)

| Key | Value |
|-----|-------|
| DB name | `salesplatform_dev` |
| DB user | `salesplatform` |
| DB password | `salesplatform` |
| JDBC URL | `jdbc:postgresql://localhost:5432/salesplatform_dev` |

These are defined in [application.yml](../../backend/src/main/resources/application.yml) and [docker-compose.yml](../../docker-compose.yml).

## Configuration Files

| File | Purpose |
|------|---------|
| `bootstrap.sql` | One-time DB/role creation |
| `backend/src/main/resources/application.yml` | Spring Boot config (datasource, Flyway, Actuator) |
| `backend/src/main/resources/db/migration/` | Flyway SQL migrations (V1–V14+) |
| `docker-compose.yml` | Containerised Postgres + pgAdmin |
| `frontend/package.json` | Next.js scripts and dependencies |

## Troubleshooting

### Backend fails to start
- Ensure PostgreSQL is running: `pg_isready -h localhost -p 5432`
- Ensure `salesplatform_dev` DB exists: run `bootstrap.sql`
- Check port 8080 is free: `netstat -an | grep 8080`

### Frontend fails to start
- Run `npm install` in `frontend/` first
- Check port 3000 is free
- Ignore the turbopack lockfile warning (cosmetic only)

## EB sync config
- `eb.sync.enabled` — default `true`; disables both push + pull
- `eb.sync.fixed-delay-ms` — default 30 min; pull cadence
- `eb.sync.writer` / `eb.sync.reader` — `logging` (default) or `jdbc` (prod)

## PO sync config
- `po.sync.enabled` — default `true`; disables push when false
- `po.sync.writer` — `logging` (default) or `jdbc` (prod)
- `po.sync.snowflake-timeout-seconds` — default 60
