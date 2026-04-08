## forgotpassword

### forgotpassword
- **Table:** `forgotpassword$forgotpassword` | **Rows:** 34,181
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 36873221954776567, 36873221951827233, 36873221951538384 |
| emailaddress | character varying(200) | Yes | aolson@planitroi.com, oanhle07131995@gmail.com, andy.huang@jytecsupply.com |
| username | character varying(200) | Yes | aolson@planitroi.com, oanhle07131995@gmail.com, andy.huang@jytecsupply.com |
| userfullname | character varying(200) | Yes | Jean Paul, N, Dom |
| forgotpasswordguid | character varying(200) | Yes | {AES3}laVJqJkmEzlUOLrp;7QoF..., {AES3}44rEBrOXhtrChVWE;TAq9..., {AES3}YeL9rei5UxptMItv;j40+... |
| forgotpasswordurl | character varying(500) | Yes | {AES3}h3y9c1XmXsf2wLGx;J/6d..., {AES3}/7J2CSJRfq7dmDLJ;ZIgR..., {AES3}xjv0fu97U0wX6bXj;5sBe... |
| validuntill | timestamp without time zone | Yes | 2025-11-05 13:01:14.539, 2025-09-24 22:17:11.457, 2025-05-14 12:00:44.762 |
| issignup | boolean | Yes | true, false |
| createddate | timestamp without time zone | Yes | 2025-10-24 00:33:57.716, 2024-12-09 16:30:30.785, 2025-03-24 15:30:32.724 |
- **PK:** id

### forgotpassword_account
- **Table:** `forgotpassword$forgotpassword_account` | **Rows:** 34,181
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| forgotpassword$forgotpasswordid | bigint | No | 36873221954776567, 36873221951827233, 36873221951538384 |
| administration$accountid | bigint | No | 23925373021624400, 23925373020676276, 23925373022147278 |
- **PK:** forgotpassword$forgotpasswordid, administration$accountid

### forgotpassword_anon_user_access
- **Table:** `forgotpassword$forgotpassword_anon_user_access` | **Rows:** 632
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| forgotpassword$forgotpasswordid | bigint | No | 36873221954357721, 36873221950203310, 36873221950209688 |
| system$userid | bigint | No | 23925373020713794, 23925373021391862, 23925373020444341 |
- **PK:** forgotpassword$forgotpasswordid, system$userid

### configuration
- **Table:** `forgotpassword$configuration` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 39125021762807149 |
- **PK:** id

### configuration_deeplink
- **Table:** `forgotpassword$configuration_deeplink` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| forgotpassword$configurationid | bigint | No | 39125021762807149 |
| deeplink$deeplinkid | bigint | No | 36310271995674839 |
- **PK:** forgotpassword$configurationid, deeplink$deeplinkid

### configuration_emailtemplate_reset
- **Table:** `forgotpassword$configuration_emailtemplate_reset` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| forgotpassword$configurationid | bigint | No | 39125021762807149 |
| email_connector$emailtemplateid | bigint | No | 46724846133994733 |
- **PK:** forgotpassword$configurationid, email_connector$emailtemplateid

### emailtemplatelanguage
- **Table:** `forgotpassword$emailtemplatelanguage` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 41095346599755991 |
- **PK:** id

### emailtemplatelanguage_emailtemplate
- **Table:** `forgotpassword$emailtemplatelanguage_emailtemplate` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| forgotpassword$emailtemplatelanguageid | bigint | No | 41095346599755991 |
| email_connector$emailtemplateid | bigint | No | 46724846133994733 |
- **PK:** forgotpassword$emailtemplatelanguageid, email_connector$emailtemplateid

### emailtemplatelanguage_language
- **Table:** `forgotpassword$emailtemplatelanguage_language` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| forgotpassword$emailtemplatelanguageid | bigint | No | 41095346599755991 |
| system$languageid | bigint | No | 13792273858822380 |
- **PK:** forgotpassword$emailtemplatelanguageid, system$languageid

### emailtemplatesmtp
- **Table:** `forgotpassword$emailtemplatesmtp` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 38562071809360007 |
- **PK:** id

### emailtemplatesmtp_emailtemplate
- **Table:** `forgotpassword$emailtemplatesmtp_emailtemplate` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| forgotpassword$emailtemplatesmtpid | bigint | No | 38562071809360007 |
| email_connector$emailtemplateid | bigint | No | 46724846133994733 |
- **PK:** forgotpassword$emailtemplatesmtpid, email_connector$emailtemplateid

### configuration_emailtemplate_signup
- **Table:** `forgotpassword$configuration_emailtemplate_signup` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| forgotpassword$configurationid | bigint | No |  |
| email_connector$emailtemplateid | bigint | No |  |
- **PK:** forgotpassword$configurationid, email_connector$emailtemplateid

### emailtemplatesmtp_emailaccount
- **Table:** `forgotpassword$emailtemplatesmtp_emailaccount` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| forgotpassword$emailtemplatesmtpid | bigint | No |  |
| email_connector$emailaccountid | bigint | No |  |
- **PK:** forgotpassword$emailtemplatesmtpid, email_connector$emailaccountid

---
