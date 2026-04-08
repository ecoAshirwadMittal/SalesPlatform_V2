## documentgeneration

### documentrequest
- **Table:** `documentgeneration$documentrequest` | **Rows:** 295
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 164662861375734677, 164662861375811295, 164662861375747570 |
| requestid | character varying(200) | Yes | 0030efef-7aa2-4845-a105-5c9..., 0170a400-5676-4286-92a7-ba7..., 02830862-29c3-4d3d-9c0e-6d6... |
| status | character varying(9) | Yes | Completed |
| filename | character varying(200) | Yes | RMA2235926031, RMA2409925001, RMA2575925003 |
| resultentity | character varying(200) | Yes | EcoATM_RMA.RMAReturnLabel |
| microflowname | character varying(200) | Yes | EcoATM_RMA.DOC_RMALabel_Print |
| contextobjectguid | bigint | Yes | 157344511981295366, 157344511981476277, 157344511981692668 |
| securitytoken | character varying(200) | Yes | {BCrypt}$2a$10$.PWiiPHUvNlQ..., {BCrypt}$2a$10$QMu7JlM.OGkV..., {BCrypt}$2a$10$eWpiosxV.wAX... |
| expirationdate | timestamp without time zone | Yes | 2026-01-21 17:19:57.686, 2025-11-25 17:30:34.525, 2025-11-20 17:53:27.667 |
| errorcode | character varying(50) | Yes |  |
| errormessage | character varying(500) | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-12-13 22:06:46.194, 2025-11-03 16:39:49.77, 2025-12-22 14:59:48.554 |
- **PK:** id

### documentrequest_documentuser
- **Table:** `documentgeneration$documentrequest_documentuser` | **Rows:** 295
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| documentgeneration$documentrequestid | bigint | No | 164662861375734677, 164662861375811295, 164662861375747570 |
| system$userid | bigint | No | 23925373020777255, 23925373021775668, 23925373021532632 |
- **PK:** documentgeneration$documentrequestid, system$userid

### documentrequest_filedocument
- **Table:** `documentgeneration$documentrequest_filedocument` | **Rows:** 295
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| documentgeneration$documentrequestid | bigint | No | 164662861375734677, 164662861375811295, 164662861375747570 |
| system$filedocumentid | bigint | No | 163536961468968143, 163536961469186323, 163536961468918065 |
- **PK:** documentgeneration$documentrequestid, system$filedocumentid

### configuration
- **Table:** `documentgeneration$configuration` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 166070236259287195 |
| deploymenttype | character varying(20) | Yes | MendixPublicCloud |
| registrationstatus | character varying(12) | Yes | Registered |
| applicationurl | character varying(200) | Yes | https://ecoatm-auctions.men... |
| accesstoken | text | Yes |  |
| accesstokenexpirationdate | timestamp without time zone | Yes | 2026-02-20 19:33:02.425 |
| refreshtoken | text | Yes |  |
| verificationtoken | character varying(200) | Yes | 3mCbO19TYp0DKr5QCCM6SGBP3qs... |
| verificationtokenexpirationdate | timestamp without time zone | Yes | 2025-10-28 20:44:31.146 |
- **PK:** id

### documentrequest_session
- **Table:** `documentgeneration$documentrequest_session` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| documentgeneration$documentrequestid | bigint | No |  |
| system$sessionid | bigint | No |  |
- **PK:** documentgeneration$documentrequestid, system$sessionid

---
