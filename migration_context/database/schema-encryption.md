## encryption

### exampleconfiguration
- **Table:** `encryption$exampleconfiguration` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| title | character varying(200) | Yes |  |
| username | character varying(200) | Yes |  |
| password | character varying(200) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
- **PK:** id

### pgpcertificate
- **Table:** `encryption$pgpcertificate` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| certificatetype | character varying(10) | Yes |  |
| passphrase_plain | character varying(20) | Yes |  |
| passphrase_encrypted | character varying(100) | Yes |  |
| reference | character varying(100) | Yes |  |
| emailaddress | character varying(50) | Yes |  |
- **PK:** id

### secretkey_publickey
- **Table:** `encryption$secretkey_publickey` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| encryption$pgpcertificateid1 | bigint | No |  |
| encryption$pgpcertificateid2 | bigint | No |  |
- **PK:** encryption$pgpcertificateid1, encryption$pgpcertificateid2

---
