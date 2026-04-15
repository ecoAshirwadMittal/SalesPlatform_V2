# SalesPlatform Modern

## Project Overview
This project is a modern Next.js and Spring Boot rebuild of the legacy Mendix SalesPlatform application.

### CRITICAL MIGRATION CONTEXT GUIDELINES
When building features, you MUST formulate your logic by referencing the exact sliced metadata extracted from the Mendix application. Do NOT hallucinate business logic.
All legacy logic is cleanly separated in the `migration_context/` directory at the root of this repository:
- **Database/Entities**: Reference `migration_context/database/queries/schema-[MODULE].md`
- **Backend APIs/Services**: Reference `migration_context/backend/` (`ACT_*.md`, `SUB_*.md`, `REST_*.md`)
- **Frontend Logic/Components**: Reference `migration_context/frontend/` (`Pages_*.md`, `MB_*.md`, `salesops_flow_home.mmd`)
- **Styling Parity (STRICT)**: Reference `migration_context/styling/` (e.g., `EcoAtm.css`) for class names, flex-box layouts, and color tokens. You MUST maintain exact css styling from the original pages. Do NOT inject modern aesthetics (glassmorphism, gradients, modern typography overrides) unless explicitly found in the original Mendix styling. Pixel-perfect cloning is required.

When planning a feature, ALWAYS start by querying the directory structure inside `migration_context/` to find the exact source files containing the legacy implementation.

## Development Rules

### Package Management
- **Frontend**: npm (Next.js)
- **Backend**: Maven (Spring Boot)
- Do not mix package managers

### Code Quality
- Type annotations required for all functions and variables
- Comment philosophy: explain "Why" not "What"

### Styling QA Verification (MANDATORY)
After building any new frontend page, you MUST visually compare the page against the live QA environment at **https://buy-qa.ecoatmdirect.com** using browser screenshots or the Playwright MCP tools. Check:
1. Layout structure (sidebar, header, content area positioning)
2. Color tokens (#407874 teal, #112d32 dark, #F7F7F7 background, etc.)
3. Font sizes, weights, and spacing match the Mendix original
4. Table/datagrid column order, widths, and cell formatting
5. Button styles, status badges, and interactive element styling
6. Modal/popup appearance if applicable

If Playwright is available, navigate to the QA page and take a screenshot, then navigate to the local page and take a screenshot, and compare them side-by-side. Fix any discrepancies before considering the page complete.

### No Mock Data
- **NEVER use mock or hardcoded data** in frontend components. Always wire to real backend API endpoints.
- If a backend endpoint does not exist yet, create it before building the frontend feature.
- Data must flow from the database through the backend API to the frontend — no shortcuts.

### Git Conventions
- **Commit format**: `[prefix]: [change description]` (feat/fix/docs/test/refactor/chore)
- **PR**: Self-review -> Assign reviewer -> Merge

## Documentation Structure
All project documents are organized under the `docs/` directory. **These documents are living artifacts** — they MUST be created and updated as we build the project.

```
docs/
├── architecture/            # Technical & architectural decisions
│   ├── overview.md          # High-level architecture
│   ├── decisions.md         # Key technical decisions log
│   ├── data-model.md        # Entity relationships, DB schema design
│   └── integration.md       # External system integrations (Salesforce, Deposco, etc.)
├── api/                     # API documentation
│   ├── index.md             # API overview, base URLs, auth scheme
│   ├── rest-endpoints.md    # All REST endpoints
│   └── error-codes.md       # Standard error codes and handling
├── app-metadata/            # Application metadata
│   ├── modules.md           # Module inventory
│   ├── constants.md         # Application constants
│   ├── enumerations.md      # All enums with values and usage context
│   └── scheduled-events.md  # Scheduled jobs, cron expressions
├── deployment/              # Deployment & operations
│   ├── setup.md             # Local dev environment setup
│   ├── deployment.md        # Deployment steps
│   ├── environments.md      # Environment configs
│   └── infrastructure.md    # Infrastructure details
├── testing/                 # Test coverage & strategy
│   ├── strategy.md          # Test strategy
│   ├── coverage.md          # Current coverage report
│   └── test-data.md         # Test data setup, fixtures
├── business-logic/          # Application business logic documentation
│   ├── index.md             # Business domain overview
│   ├── auction-flow.md      # Auction/bidding business rules
│   ├── buyer-management.md  # Buyer codes, qualifications, pricing logic
│   ├── user-auth.md         # Authentication, authorization, roles
│   └── data-import.md       # Data import/export processes
├── requirements/            # Requirements definition
├── design/                  # Design documents
├── tasks/                   # Task management
├── adr/                     # Architecture Decision Records
├── specs/                   # Implementation specifications
└── test-specs/              # Test specifications
```

### Documentation Rules (MANDATORY)
When building any feature, update the relevant docs:

1. **New API endpoint** -> Update `docs/api/rest-endpoints.md`
2. **Architecture/tech decision** -> Update `docs/architecture/decisions.md`
3. **New module or entity** -> Update `docs/app-metadata/modules.md` and `docs/architecture/data-model.md`
4. **Business rule implementation** -> Update the relevant file in `docs/business-logic/`
5. **Deployment change** -> Update `docs/deployment/deployment.md`
6. **Test added/coverage change** -> Update `docs/testing/coverage.md`
7. **New constant/enum** -> Update `docs/app-metadata/constants.md` or `enumerations.md`
8. **Any plan produced** (implementation plan, refactor plan, phased roadmap, task breakdown) -> **ALWAYS** persist it as a markdown file under `docs/tasks/` (or `docs/adr/` for architecture decisions) before starting implementation. Never leave a plan only in conversation — plans must be durable artifacts that future sessions can reference and update.

## Dev Test Credentials

### Sample Login Accounts
Seeded by Flyway migration `V15__seed_dev_roles_and_users.sql` — available on any fresh `salesplatform_dev` database.

| Role | Email | Password |
|------|-------|----------|
| Administrator | `admin@test.com` | `Admin123!` |
| Co-Admin | `coadmin@test.com` | `CoAdmin123!` |
| SalesOps | `salesops@test.com` | `SalesOps123!` |
| SalesRep | `salesrep@test.com` | `SalesRep123!` |
| Bidder | `bidder@buyerco.com` | `Bidder123!` |
| ecoAtmDirectAdmin | `directadmin@test.com` | `Direct123!` |

- **Login URL**: http://localhost:3000/login
- **Auth endpoint**: `POST /api/v1/auth/login` `{"email","password","rememberMe"}`
- Non-`@ecoatm.com` emails are used so the frontend doesn't redirect to Azure AD SSO
- `@ecoatm.com` emails trigger SSO redirect (Employee Login flow)
- Passwords are BCrypt-hashed in `identity.users`; Spring `BCryptPasswordEncoder` validates them

### QA Environment (Buyer / PWS Layout)
- **URL**: `https://buy-qa.ecoatmdirect.com/p/login/web`
- **Email**: `nadia.ecoatm@gmail.com`
- **Password**: `Test100%`
- **Buyer code**: `NB_PWS`

### Database Credentials (Dev)
| Key | Value |
|-----|-------|
| DB name | `salesplatform_dev` |
| DB user / password | `salesplatform` / `salesplatform` |
| JDBC URL | `jdbc:postgresql://localhost:5432/salesplatform_dev` |

## Data Migration from Mendix

### Migration Generator Script
`migration_scripts/extract_qa_data.py` extracts data from a Mendix source database and generates Flyway SQL migration files (V16-V24). Mendix IDs are NOT preserved — new sequential IDs are assigned with FK remapping.

```bash
cd migration_scripts

# Generate from QA database
python extract_qa_data.py --source-db qa-0327

# Generate from Production database
python extract_qa_data.py --source-db prod-0325
```

### Generated Flyway Files (V16-V24)
| File | Schema | Key Tables | Row Counts |
|------|--------|------------|------------|
| V16 | identity | languages (1), timezones (519), user_roles (11), grantable_roles (13) | 544 |
| V17 | identity | users (487+6 dev), accounts (481), role_assignments (559+6), user_languages, user_timezones | ~2,000 |
| V18 | buyer_mgmt | sales_reps (3), buyers (579), buyer_codes (653), buyer_sales_reps, buyer_code_buyers, change_logs, auctions_feature_config | ~1,960 |
| V19 | user_mgmt | ecoatm_direct_users (467), user_buyers (423) | 890 |
| V20 | sso | saml_authn_contexts (26), x509_certificates (2), idp_metadata, sp_metadata, sso_config, forgot_password | ~32 |
| V21 | mdm | brand (17), category (3), model (585), condition (59), capacity (131), carrier (32), color (192), grade (8), device (22,476), price_history (2,997) | ~26,500 |
| V22 | pws | offers (1,585 unified), offer_items (12,478), orders (1,612), shipment_details (202) | ~15,877 |
| V23 | buyer_mgmt | qualified_buyer_codes (378,755), qbc_buyer_codes, qbc_scheduling_auctions, qbc_bid_rounds | ~381,000 |
| V24 | integration | deposco_config (1), error_mappings (27) | 28 |

### Applying Migrations (Fresh DB)
```bash
# 1. Drop and recreate database
psql -U postgres -f bootstrap.sql

# 2. Start backend (Flyway auto-applies V1-V24)
cd backend && mvn clean spring-boot:run
```

### What is NOT Migrated
- Mendix sessions, password reset tokens
- SAML requests/responses, SSO audit logs
- Integration API access tokens (~2.7M rows) and API logs (~350K rows)
- Orphaned offer items (no linked offer in junction table)

### Source DB Conventions (Mendix)
- Tables: `module$entity` (e.g., `system$user`, `ecoatm_buyermanagement$buyercode`)
- Junction tables: `module$entity_association` (e.g., `ecoatm_pws$offeritem_offer`)
- Columns: lowercase, no module prefix
- Connection: `PGPASSWORD='Agarwal1$' psql -h localhost -U postgres -d <db-name>`

## Related Documents
- Architecture overview: @docs/architecture/overview.md
- Technical decisions: @docs/architecture/decisions.md
- Data model: @docs/architecture/data-model.md
- API documentation: @docs/api/index.md | @docs/api/rest-endpoints.md
- App metadata: @docs/app-metadata/modules.md | @docs/app-metadata/constants.md
- Deployment guide: @docs/deployment/setup.md | @docs/deployment/deployment.md
- Test strategy & coverage: @docs/testing/strategy.md | @docs/testing/coverage.md
- Business logic: @docs/business-logic/index.md
- Development rules: @docs/development-rules.md
- ADR template: @docs/adr/template.md
