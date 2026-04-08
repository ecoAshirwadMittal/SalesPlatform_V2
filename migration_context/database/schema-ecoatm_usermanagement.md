## ecoatm_usermanagement

### ecoatmdirectuser
- **Table:** `ecoatm_usermanagement$ecoatmdirectuser` | **Rows:** 1,088
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 23925373022671603, 23925373022147278, 23925373021021319 |
| submissionid | bigint | Yes | 877, 551, 909 |
| firstname | character varying(200) | Yes | Iryna, Greg, Ibrahim |
| lastname | character varying(200) | Yes | Sutton, Serio, Cholagh |
| inviteddate | timestamp without time zone | Yes | 2025-02-19 16:30:22.748, 2026-01-07 18:44:41.816, 2025-09-23 15:49:57.378 |
| lastinvitesent | timestamp without time zone | Yes | 2024-05-21 23:30:45.371, 2025-02-19 16:30:22.748, 2026-01-07 18:44:41.816 |
| activationdate | timestamp without time zone | Yes | 2025-08-22 20:10:16.961, 2025-07-17 18:03:35.003, 2025-02-14 17:29:29.634 |
| password_tmp | character varying(200) | Yes |  |
| passwordconfirm_tmp | character varying(200) | Yes |  |
| isbuyerrole | boolean | Yes | true, false |
| userbuyerdisplay | character varying(400) | Yes | Nossik LLC, Luckys Trade Company Limited, Bright Systems dba Technocell  |
| userrolesdisplay | character varying(200) | Yes | Administrator, SalesRep, Co..., Bidder, SalesOps |
| userstatus | character varying(8) | Yes | Disabled, Active |
| inactive | boolean | Yes | true, false |
| overalluserstatus | character varying(8) | Yes | Active, Disabled, Inactive |
| landingpagepreference | character varying(17) | Yes | Wholesale_Auction, Premium_Wholesale |
| entityowner | character varying(200) | Yes | aljaraselectronics@gmail.com, ekomventures@gmail.com, corbalinc26@gmail.com |
| entitychanger | character varying(200) | Yes | Anonymous_c0028d14-5763-44c..., Anonymous_a8296a0e-3d5d-4c0..., Anonymous_fc86f28b-5935-40e... |
| acknowledgement | boolean | Yes | false |
- **PK:** id

### ecoatmdirectuser_buyer
- **Table:** `ecoatm_usermanagement$ecoatmdirectuser_buyer` | **Rows:** 1,061
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_usermanagement$ecoatmdirectuserid | bigint | No | 23925373022671603, 23925373022147278, 23925373021023007 |
| ecoatm_buyermanagement$buyerid | bigint | No | 43628621390561926, 43628621390971400, 43628621390536716 |
- **PK:** ecoatm_usermanagement$ecoatmdirectuserid, ecoatm_buyermanagement$buyerid

### ecoatmdirectuser_userstatus
- **Table:** `ecoatm_usermanagement$ecoatmdirectuser_userstatus` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| ecoatm_usermanagement$ecoatmdirectuserid | bigint | No |  |
| ecoatm_usermanagement$userstatusid | bigint | No |  |
- **PK:** ecoatm_usermanagement$ecoatmdirectuserid, ecoatm_usermanagement$userstatusid

### userstatus
- **Table:** `ecoatm_usermanagement$userstatus` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| userstatus | character varying(8) | Yes |  |
| statustext | character varying(200) | Yes |  |
- **PK:** id

---
