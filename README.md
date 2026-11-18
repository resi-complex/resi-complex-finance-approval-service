# resi-complex-finance-approval-service

Microservicio de Aprobación de Gastos Extraordinarios y Votación del Consejo de Administración para **resi-complex**.

## Configuración
- **Puerto:** `8088`
- **Base de datos MySQL:** `finance_db`
- **Migraciones Flyway:** `src/main/resources/db/migration/V1__init_finance.sql`
- **Swagger UI:** `http://localhost:8088/swagger-ui/index.html`

## Endpoints principales (`/api/v1/expense-proposals`)
- `POST /api/v1/expense-proposals` — Crear propuesta de gasto extraordinario (`201 Created`)
- `GET /api/v1/expense-proposals` — Listar propuestas (filtro opcional `?status=PENDING_REVIEW|APPROVED|REJECTED`)
- `GET /api/v1/expense-proposals/{id}` — Consultar detalle de propuesta e historial de votos
- `POST /api/v1/expense-proposals/{id}/votes` — Emitir voto del Consejo (`APPROVED` / `REJECTED`) (`201 Created`)
- `GET /api/v1/expense-proposals/{id}/votes` — Listar votos inmutables de una propuesta
