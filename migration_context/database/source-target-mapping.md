# Source-to-Target Database Mapping

> **Generated:** 2026-04-05
> **Source DB:** `qa-0327` (Mendix legacy, PostgreSQL, user: `postgres`)
> **Target DB:** `salesplatform_dev` (Spring Boot rebuild, PostgreSQL, user: `salesplatform`)

---

## Summary

| Target Schema | Target Tables | Mapped Source Tables |
|--------------|---------------|---------------------|
| identity | 12 tables | system$user, system$userrole, system$session, system$language, system$timezone, administration$account + association tables |
| user_mgmt | 4 tables | ecoatm_usermanagement$ecoatmdirectuser, ecoatm_usermanagement$userstatus + association tables |
| buyer_mgmt | 20 tables | ecoatm_buyermanagement$* (buyer, buyercode, qualifiedbuyercodes, salesrepresentative, auctionsfeature, etc.) + association/helper tables |
| sso | 15 tables | saml20$ssoconfiguration, saml20$spmetadata, saml20$keystore, saml20$idpmetadata, saml20$ssolog, saml20$samlauthncontext, saml20$samlrequest, saml20$samlresponse, saml20$x509certificate, forgotpassword$* |
| pws | 4 tables | ecoatm_pws$offer, ecoatm_pws$offeritem, ecoatm_pws$order, ecoatm_pws$shipmentdetail + association tables |
| mdm | 10 tables | ecoatm_pwsmdm$device, brand, carrier, model, capacity, color, condition, category, grade, pricehistory + association tables |
| integration | 5 tables | ecoatm_pwsintegration$integration, deposcoconfig, accesstoken, pwsresponseconfig, pwsconfiguration |

---

## Schema: `identity`

### identity.users

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `system$user` | id | PK |
| user_type | varchar | `system$user` | submetaobjectname | Mendix subclass discriminator |
| name | varchar | `system$user` | name | Username/email |
| password | varchar | `system$user` | password | Hashed password |
| last_login | timestamp | `system$user` | lastlogin | |
| blocked | boolean | `system$user` | blocked | |
| blocked_since | timestamp | `system$user` | blockedsince | |
| active | boolean | `system$user` | active | |
| failed_logins | integer | `system$user` | failedlogins | |
| web_service_user | boolean | `system$user` | webserviceuser | |
| is_anonymous | boolean | `system$user` | isanonymous | |
| created_date | timestamp | `system$user` | createddate | |
| changed_date | timestamp | `system$user` | changeddate | |
| owner_id | bigint | `system$user` | system$owner | FK to users |
| changed_by_id | bigint | `system$user` | system$changedby | FK to users |

**Source rows:** 487

### identity.accounts

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| user_id | bigint | `administration$account` | id | PK, FK to identity.users |
| full_name | varchar | `administration$account` | fullname | |
| email | varchar | `administration$account` | email | |
| is_local_user | boolean | `administration$account` | islocaluser | |

**Source rows:** 481

### identity.user_roles

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `system$userrole` | id | PK |
| model_guid | varchar | `system$userrole` | modelguid | Mendix model GUID |
| name | varchar | `system$userrole` | name | Role name |
| description | varchar | `system$userrole` | description | |

**Source rows:** 11

### identity.user_role_assignments (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| user_id | bigint | `system$userroles` | system$userid | FK to users |
| role_id | bigint | `system$userroles` | system$userroleid | FK to user_roles |

**Source rows:** 559

### identity.grantable_roles (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| grantor_role_id | bigint | `system$grantableroles` | system$userroleid1 | FK to user_roles |
| grantee_role_id | bigint | `system$grantableroles` | system$userroleid2 | FK to user_roles |

**Source rows:** 13

### identity.sessions

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `system$session` | id | PK |
| session_id | varchar | `system$session` | sessionid | |
| csrf_token | varchar | `system$session` | csrftoken | |
| last_active | timestamp | `system$session` | lastactive | |
| long_lived | boolean | `system$session` | longlived | |
| read_only_hash_key | varchar | `system$session` | readonlyhashkey | |
| last_action_execution | timestamp | `system$session` | lastactionexecution | |
| created_date | timestamp | `system$session` | createddate | |

**Source rows:** 1

### identity.session_users (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| session_id | bigint | `system$session_user` | system$sessionid | FK to sessions |
| user_id | bigint | `system$session_user` | system$userid | FK to users |

**Source rows:** 1

### identity.languages

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `system$language` | id | PK |
| code | varchar | `system$language` | code | e.g. "en_US" |
| description | varchar | `system$language` | description | |

**Source rows:** 1

### identity.user_languages (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| user_id | bigint | `system$user_language` | system$userid | FK to users |
| language_id | bigint | `system$user_language` | system$languageid | FK to languages |

**Source rows:** 468

### identity.timezones

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `system$timezone` | id | PK |
| code | varchar | `system$timezone` | code | e.g. "America/Los_Angeles" |
| description | varchar | `system$timezone` | description | |
| raw_offset | integer | `system$timezone` | rawoffset | Offset in ms from UTC |

**Source rows:** 519

### identity.user_timezones (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| user_id | bigint | `system$user_timezone` | system$userid | FK to users |
| timezone_id | bigint | `system$user_timezone` | system$timezoneid | FK to timezones |

**Source rows:** 487

### identity.flyway_schema_history

**No source mapping** -- Flyway migration tracking table, auto-managed by Spring Boot.

---

## Schema: `user_mgmt`

### user_mgmt.ecoatm_direct_users

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| user_id | bigint | `ecoatm_usermanagement$ecoatmdirectuser` | id | PK, FK to identity.users (Mendix specialization of system$user) |
| submission_id | bigint | `ecoatm_usermanagement$ecoatmdirectuser` | submissionid | |
| first_name | varchar | `ecoatm_usermanagement$ecoatmdirectuser` | firstname | |
| last_name | varchar | `ecoatm_usermanagement$ecoatmdirectuser` | lastname | |
| invited_date | timestamp | `ecoatm_usermanagement$ecoatmdirectuser` | inviteddate | |
| last_invite_sent | timestamp | `ecoatm_usermanagement$ecoatmdirectuser` | lastinvitesent | |
| activation_date | timestamp | `ecoatm_usermanagement$ecoatmdirectuser` | activationdate | |
| password_tmp | varchar | `ecoatm_usermanagement$ecoatmdirectuser` | password_tmp | Temporary password |
| password_confirm_tmp | varchar | `ecoatm_usermanagement$ecoatmdirectuser` | passwordconfirm_tmp | |
| is_buyer_role | boolean | `ecoatm_usermanagement$ecoatmdirectuser` | isbuyerrole | |
| user_status | varchar | `ecoatm_usermanagement$ecoatmdirectuser` | userstatus | Enum string |
| inactive | boolean | `ecoatm_usermanagement$ecoatmdirectuser` | inactive | |
| overall_user_status | varchar | `ecoatm_usermanagement$ecoatmdirectuser` | overalluserstatus | Enum string |
| landing_page_preference | varchar | `ecoatm_usermanagement$ecoatmdirectuser` | landingpagepreference | |
| acknowledgement | boolean | `ecoatm_usermanagement$ecoatmdirectuser` | acknowledgement | |
| created_date | timestamp | `ecoatm_usermanagement$ecoatmdirectuser` | -- | *Not in source; use Mendix created date from system$user* |
| changed_date | timestamp | `ecoatm_usermanagement$ecoatmdirectuser` | -- | *Not in source; use Mendix changed date from system$user* |
| owner_id | bigint | `ecoatm_usermanagement$ecoatmdirectuser` | -- | *Not in source; derive from system$user* |
| changed_by_id | bigint | `ecoatm_usermanagement$ecoatmdirectuser` | -- | *Not in source; derive from system$user* |

**Note:** Source has extra UI-helper columns not migrated: `userbuyerdisplay`, `userrolesdisplay`, `entityowner`, `entitychanger`.

**Source rows:** 467

### user_mgmt.user_buyers (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| user_id | bigint | `ecoatm_usermanagement$ecoatmdirectuser_buyer` | ecoatm_usermanagement$ecoatmdirectuserid | FK to ecoatm_direct_users |
| buyer_id | bigint | `ecoatm_usermanagement$ecoatmdirectuser_buyer` | ecoatm_buyermanagement$buyerid | FK to buyer_mgmt.buyers |

**Source rows:** 423

### user_mgmt.user_statuses

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_usermanagement$userstatus` | id | PK |
| user_status | varchar | `ecoatm_usermanagement$userstatus` | userstatus | |
| status_text | varchar | `ecoatm_usermanagement$userstatus` | statustext | |

**Source rows:** 0 (empty)

### user_mgmt.user_status_assignments (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| user_id | bigint | `ecoatm_usermanagement$ecoatmdirectuser_userstatus` | ecoatm_usermanagement$ecoatmdirectuserid | FK |
| user_status_id | bigint | `ecoatm_usermanagement$ecoatmdirectuser_userstatus` | ecoatm_usermanagement$userstatusid | FK |

**Source rows:** 0 (empty)

---

## Schema: `buyer_mgmt`

### buyer_mgmt.buyers

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_buyermanagement$buyer` | id | PK |
| submission_id | bigint | `ecoatm_buyermanagement$buyer` | submissionid | |
| company_name | varchar | `ecoatm_buyermanagement$buyer` | companyname | |
| status | varchar | `ecoatm_buyermanagement$buyer` | status | |
| is_special_buyer | boolean | `ecoatm_buyermanagement$buyer` | isspecialbuyer | |
| created_date | timestamp | `ecoatm_buyermanagement$buyer` | createddate | |
| changed_date | timestamp | `ecoatm_buyermanagement$buyer` | changeddate | |
| owner_id | bigint | `ecoatm_buyermanagement$buyer` | system$owner | |
| changed_by_id | bigint | `ecoatm_buyermanagement$buyer` | system$changedby | |
| entity_owner | varchar | `ecoatm_buyermanagement$buyer` | entityowner | |

**Note:** Source has extra UI-helper columns not migrated: `buyercodesdisplay`, `isfailedbuyerdisable`, various validation message columns, `entitychanger`.

**Source rows:** 579

### buyer_mgmt.buyer_codes

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_buyermanagement$buyercode` | id | PK |
| submission_id | bigint | `ecoatm_buyermanagement$buyercode` | submissionid | |
| code | varchar | `ecoatm_buyermanagement$buyercode` | code | Buyer code string |
| buyer_code_type | varchar | `ecoatm_buyermanagement$buyercode` | buyercodetype | Enum |
| status | varchar | `ecoatm_buyermanagement$buyercode` | status | Enum |
| budget | integer | `ecoatm_buyermanagement$buyercode` | budget | |
| soft_delete | boolean | `ecoatm_buyermanagement$buyercode` | softdelete | |
| created_date | timestamp | `ecoatm_buyermanagement$buyercode` | createddate | |
| changed_date | timestamp | `ecoatm_buyermanagement$buyercode` | changeddate | |
| owner_id | bigint | `ecoatm_buyermanagement$buyercode` | system$owner | |
| changed_by_id | bigint | `ecoatm_buyermanagement$buyercode` | system$changedby | |
| entity_owner | varchar | `ecoatm_buyermanagement$buyercode` | entityowner | |

**Note:** Source has extra UI-helper columns not migrated: `typevalid`, `codeemptyvalid`, `codeuniquevalid`, `entitychanger`, `showsubmitofferbtn`.

**Source rows:** 653

### buyer_mgmt.buyer_code_buyers (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| buyer_code_id | bigint | `ecoatm_buyermanagement$buyercode_buyer` | ecoatm_buyermanagement$buyercodeid | FK |
| buyer_id | bigint | `ecoatm_buyermanagement$buyercode_buyer` | ecoatm_buyermanagement$buyerid | FK |

**Source rows:** 653

### buyer_mgmt.sales_representatives

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_buyermanagement$salesrepresentative` | id | PK |
| sales_representative_id | bigint | `ecoatm_buyermanagement$salesrepresentative` | salesrepresentativeid | External ID |
| first_name | varchar | `ecoatm_buyermanagement$salesrepresentative` | salesrepfirstname | |
| last_name | varchar | `ecoatm_buyermanagement$salesrepresentative` | salesreplastname | |
| active | boolean | `ecoatm_buyermanagement$salesrepresentative` | active | |
| created_date | timestamp | `ecoatm_buyermanagement$salesrepresentative` | createddate | |
| changed_date | timestamp | `ecoatm_buyermanagement$salesrepresentative` | changeddate | |
| owner_id | bigint | `ecoatm_buyermanagement$salesrepresentative` | system$owner | |
| changed_by_id | bigint | `ecoatm_buyermanagement$salesrepresentative` | system$changedby | |

**Source rows:** 3

### buyer_mgmt.buyer_sales_reps (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| buyer_id | bigint | `ecoatm_buyermanagement$buyer_salesrepresentative` | ecoatm_buyermanagement$buyerid | FK |
| sales_rep_id | bigint | `ecoatm_buyermanagement$buyer_salesrepresentative` | ecoatm_buyermanagement$salesrepresentativeid | FK |

**Source rows:** 30

### buyer_mgmt.qualified_buyer_codes

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes` | id | PK |
| qualification_type | varchar | `ecoatm_buyermanagement$qualifiedbuyercodes` | qualificationtype | Enum |
| included | boolean | `ecoatm_buyermanagement$qualifiedbuyercodes` | included | |
| submitted | boolean | `ecoatm_buyermanagement$qualifiedbuyercodes` | submitted | |
| submitted_datetime | timestamp | `ecoatm_buyermanagement$qualifiedbuyercodes` | submitteddatetime | |
| opened_dashboard | boolean | `ecoatm_buyermanagement$qualifiedbuyercodes` | openeddashboard | |
| opened_dashboard_datetime | timestamp | `ecoatm_buyermanagement$qualifiedbuyercodes` | openeddashboarddatetime | |
| is_special_treatment | boolean | `ecoatm_buyermanagement$qualifiedbuyercodes` | isspecialtreatment | |
| created_date | timestamp | `ecoatm_buyermanagement$qualifiedbuyercodes` | createddate | |
| changed_date | timestamp | `ecoatm_buyermanagement$qualifiedbuyercodes` | changeddate | |
| owner_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes` | system$owner | |
| changed_by_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes` | system$changedby | |

**Source rows:** 378,755

### buyer_mgmt.qbc_bid_rounds (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| qualified_buyer_code_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes_bidround` | ecoatm_buyermanagement$qualifiedbuyercodesid | FK |
| bid_round_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes_bidround` | auctionui$bidroundid | FK (cross-module to auctionui) |

**Source rows:** 20

### buyer_mgmt.qbc_buyer_codes (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| qualified_buyer_code_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes_buyercode` | ecoatm_buyermanagement$qualifiedbuyercodesid | FK |
| buyer_code_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes_buyercode` | ecoatm_buyermanagement$buyercodeid | FK |

**Source rows:** 377,986

### buyer_mgmt.qbc_scheduling_auctions (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| qualified_buyer_code_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes_schedulingauction` | ecoatm_buyermanagement$qualifiedbuyercodesid | FK |
| scheduling_auction_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes_schedulingauction` | auctionui$schedulingauctionid | FK (cross-module) |

**Source rows:** 2,192

### buyer_mgmt.qbc_submitted_by (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| qualified_buyer_code_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes_submittedby` | ecoatm_buyermanagement$qualifiedbuyercodesid | FK |
| user_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodes_submittedby` | system$userid | FK to identity.users |

**Source rows:** 0 (empty)

### buyer_mgmt.qbc_query_helpers

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper` | id | PK |

**Source rows:** 472

### buyer_mgmt.qbc_query_helper_auctions (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| query_helper_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper_auction` | ecoatm_buyermanagement$qualifiedbuyercodesqueryhelperid | FK |
| auction_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper_auction` | auctionui$auctionid | FK (cross-module) |

**Source rows:** 2

### buyer_mgmt.qbc_query_helper_sessions (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| query_helper_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper_session` | ecoatm_buyermanagement$qualifiedbuyercodesqueryhelperid | FK |
| session_id | bigint | `ecoatm_buyermanagement$qualifiedbuyercodesqueryhelper_session` | system$sessionid | FK to identity.sessions |

**Source rows:** 0 (empty)

### buyer_mgmt.buyer_code_change_logs

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_buyermanagement$buyercodechangelog` | id | PK |
| buyer_code_id | bigint | `ecoatm_buyermanagement$buyercodechangelog_buyercode` | ecoatm_buyermanagement$buyercodeid | FK (via association table) |
| old_buyer_code_type | varchar | `ecoatm_buyermanagement$buyercodechangelog` | oldbuyercodetype | |
| new_buyer_code_type | varchar | `ecoatm_buyermanagement$buyercodechangelog` | newbuyercodetype | |
| edited_by | varchar | `ecoatm_buyermanagement$buyercodechangelog` | editedby | |
| edited_on | timestamp | `ecoatm_buyermanagement$buyercodechangelog` | editedon | |
| created_date | timestamp | `ecoatm_buyermanagement$buyercodechangelog` | createddate | |
| changed_date | timestamp | `ecoatm_buyermanagement$buyercodechangelog` | changeddate | |
| owner_id | bigint | `ecoatm_buyermanagement$buyercodechangelog` | system$owner | |
| changed_by_id | bigint | `ecoatm_buyermanagement$buyercodechangelog` | system$changedby | |

**Note:** In source, `buyer_code_id` comes from association table `buyercodechangelog_buyercode` (18 rows). Target denormalizes this into a direct FK column.

**Source rows:** 37 (change log) / 18 (association links)

### buyer_mgmt.buyer_code_select_helpers

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_buyermanagement$buyercodeselect_helper` | id | PK |
| company_name | varchar | `ecoatm_buyermanagement$buyercodeselect_helper` | companyname | |
| code | varchar | `ecoatm_buyermanagement$buyercodeselect_helper` | code | |
| is_upsell_round | boolean | `ecoatm_buyermanagement$buyercodeselect_helper` | isupsellround | |
| note | varchar | `ecoatm_buyermanagement$buyercodeselect_helper` | note | |

**Source rows:** 1

### buyer_mgmt.buyer_code_select_helper_bid_rounds (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| helper_id | bigint | `ecoatm_buyermanagement$buyercodeselect_helper_bidround` | ecoatm_buyermanagement$buyercodeselect_helperid | FK |
| bid_round_id | bigint | `ecoatm_buyermanagement$buyercodeselect_helper_bidround` | auctionui$bidroundid | FK (cross-module) |

**Source rows:** 0

### buyer_mgmt.buyer_code_select_helper_buyer_codes (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| helper_id | bigint | `ecoatm_buyermanagement$buyercodeselect_helper_buyercode` | ecoatm_buyermanagement$buyercodeselect_helperid | FK |
| buyer_code_id | bigint | `ecoatm_buyermanagement$buyercodeselect_helper_buyercode` | ecoatm_buyermanagement$buyercodeid | FK |

**Source rows:** 0

### buyer_mgmt.buyer_code_select_helper_scheduling_auctions (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| helper_id | bigint | `ecoatm_buyermanagement$buyercodeselect_helper_schedulingauction` | ecoatm_buyermanagement$buyercodeselect_helperid | FK |
| scheduling_auction_id | bigint | `ecoatm_buyermanagement$buyercodeselect_helper_schedulingauction` | auctionui$schedulingauctionid | FK (cross-module) |

**Source rows:** 0

### buyer_mgmt.buyer_code_session_helpers

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_buyermanagement$buyercode_sessionandtabhelper` | id | PK |
| tab_id | varchar | `ecoatm_buyermanagement$buyercode_sessionandtabhelper` | tabid | |
| created_date | timestamp | `ecoatm_buyermanagement$buyercode_sessionandtabhelper` | createddate | |
| changed_date | timestamp | `ecoatm_buyermanagement$buyercode_sessionandtabhelper` | changeddate | |
| owner_id | bigint | `ecoatm_buyermanagement$buyercode_sessionandtabhelper` | system$owner | |
| changed_by_id | bigint | `ecoatm_buyermanagement$buyercode_sessionandtabhelper` | system$changedby | |

**Source rows:** 0 (empty)

### buyer_mgmt.buyer_code_session_helper_codes (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| helper_id | bigint | `ecoatm_buyermanagement$buyercode_sessionandtabhelper_buyercode` | ecoatm_buyermanagement$buyercode_sessionandtabhelperid | FK |
| buyer_code_id | bigint | `ecoatm_buyermanagement$buyercode_sessionandtabhelper_buyercode` | ecoatm_buyermanagement$buyercodeid | FK |

**Source rows:** 0

### buyer_mgmt.buyer_code_session_helper_sessions (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| helper_id | bigint | `ecoatm_buyermanagement$buyercode_sessionandtabhelper_session` | ecoatm_buyermanagement$buyercode_sessionandtabhelperid | FK |
| session_id | bigint | `ecoatm_buyermanagement$buyercode_sessionandtabhelper_session` | system$sessionid | FK |

**Source rows:** 0

### buyer_mgmt.auctions_feature_config

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_buyermanagement$auctionsfeature` | id | PK |
| auction_round2_minutes_offset | integer | `ecoatm_buyermanagement$auctionsfeature` | auctionround2minutesoffset | |
| auction_round3_minutes_offset | integer | `ecoatm_buyermanagement$auctionsfeature` | auctionround3minutesoffset | |
| send_buyer_to_snowflake | boolean | `ecoatm_buyermanagement$auctionsfeature` | sendbuyertosnowflake | |
| send_bid_data_to_snowflake | boolean | `ecoatm_buyermanagement$auctionsfeature` | sendbiddatatosnowflake | |
| send_auction_data_to_snowflake | boolean | `ecoatm_buyermanagement$auctionsfeature` | sendauctiondatatosnowflake | |
| send_bid_ranking_to_snowflake | boolean | `ecoatm_buyermanagement$auctionsfeature` | sendbidrankingtosnowflake | |
| create_excel_bid_export | boolean | `ecoatm_buyermanagement$auctionsfeature` | createexcelbidexport | |
| generate_round3_files | boolean | `ecoatm_buyermanagement$auctionsfeature` | generateround3files | |
| calculate_round2_buyer_participation | boolean | `ecoatm_buyermanagement$auctionsfeature` | calculateround2buyerparticipation | |
| send_files_to_sharepoint_on_submit | boolean | `ecoatm_buyermanagement$auctionsfeature` | sendfilestosharepointonsubmit | |
| sp_retry_count | integer | `ecoatm_buyermanagement$auctionsfeature` | spretrycount | |
| round2_criteria_active | boolean | `ecoatm_buyermanagement$auctionsfeature` | round2criteriaactive | |
| minimum_allowed_bid | numeric | `ecoatm_buyermanagement$auctionsfeature` | minimumallowedbid | |
| legacy_auction_dashboard_active | boolean | -- | -- | **NEW in target** (no direct source column; possibly derives from multiple legacy flags) |
| require_wholesale_user_agreement | boolean | `ecoatm_buyermanagement$auctionsfeature` | requirewholesaleuseragreement | |
| created_date | timestamp | `ecoatm_buyermanagement$auctionsfeature` | createddate | |
| changed_date | timestamp | `ecoatm_buyermanagement$auctionsfeature` | changeddate | |
| owner_id | bigint | `ecoatm_buyermanagement$auctionsfeature` | system$owner | |
| changed_by_id | bigint | `ecoatm_buyermanagement$auctionsfeature` | system$changedby | |

**Note:** Source has extra legacy flags not in target: `legacyroundthree`, `legacybiddatacreation`, `legacytargetprice`, `legacymanualqualification`.

**Source rows:** 1

### buyer_mgmt.buyer_round3_helpers (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| buyer_id | bigint | `ecoatm_buyermanagement$buyer_round3nosalesrephelper` | ecoatm_buyermanagement$buyerid | FK |
| helper_id | bigint | `ecoatm_buyermanagement$buyer_round3nosalesrephelper` | ecoatm_buyermanagement$round3nosalesrephelperid | FK |

**Source rows:** 0 (empty)

### buyer_mgmt.round3_no_sales_rep_helpers

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_buyermanagement$round3nosalesrephelper` | id | PK |

**Source rows:** 0 (empty)

---

## Schema: `pws`

### pws.offer

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pws$offer` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** (reference to legacy Mendix ID if needed) |
| offer_type | varchar | `ecoatm_pws$offer` | offertype | |
| status | varchar | `ecoatm_pws$offer` | offerstatus | |
| total_qty | integer | `ecoatm_pws$offer` | offertotalquantity | |
| total_price | numeric | `ecoatm_pws$offer` | offertotalprice | Source is integer, target is numeric |
| buyer_code_id | bigint | `ecoatm_pws$offer_buyercode` | ecoatm_buyermanagement$buyercodeid | FK (via association table) |
| sales_rep_id | bigint | `ecoatm_pws$offer_salesrepresentative` | ecoatm_buyermanagement$salesrepresentativeid | FK (via association table) |
| submission_date | timestamp | `ecoatm_pws$offer` | offersubmissiondate | |
| sales_review_completed_on | timestamp | `ecoatm_pws$offer` | salesreviewcompletedon | |
| canceled_on | timestamp | `ecoatm_pws$offer` | offercancelledon | |
| created_date | timestamp | `ecoatm_pws$offer` | createddate | |
| updated_date | timestamp | `ecoatm_pws$offer` | changeddate | |

**Note:** Source has many extra columns not migrated: `finaloffertotalqty`, `isvalidoffer`, `finaloffersubmittedon`, `offeravgprice`, `finaloffertotalsku`, `finaloffertotalprice`, `counteroffertotalqty`, various counter-offer fields, `offerid` (string), `sameskuoffer`, `offerbeyondsla`, `offerreverteddate`, `buyeroffercancelled`, `selleroffercancelled`, reminder flags, device type flags, shipped/order quantities.

**Source rows:** 1,168 (offer) / 1,165 (offer_buyercode) / 506 (offer_salesrep)

### pws.offer_item

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pws$offeritem` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| offer_id | bigint | `ecoatm_pws$offeritem_offer` | ecoatm_pws$offerid | FK (via association table) |
| sku | varchar | -- | -- | **Derived** from device association |
| device_id | bigint | `ecoatm_pws$offeritem_device` | ecoatm_pwsmdm$deviceid | FK (via association table) |
| quantity | integer | `ecoatm_pws$offeritem` | offerquantity | |
| price | numeric | `ecoatm_pws$offeritem` | offerprice | Source is integer |
| total_price | numeric | `ecoatm_pws$offeritem` | offertotalprice | Source is integer |
| counter_qty | integer | `ecoatm_pws$offeritem` | counterquantity | |
| counter_price | numeric | `ecoatm_pws$offeritem` | counterprice | Source is integer |
| counter_total | numeric | `ecoatm_pws$offeritem` | countertotal | Source is integer |
| item_status | varchar | `ecoatm_pws$offeritem` | salesofferitemstatus | |
| created_date | timestamp | `ecoatm_pws$offeritem` | createddate | |
| updated_date | timestamp | `ecoatm_pws$offeritem` | changeddate | |

**Note:** Source has extra columns not migrated: `buyercounterstatus`, `minpercentage`, `listpercentage`, `finalofferquantity`, `finaloffertotalprice`, `finalofferprice`, `sameskuofferavailable`, `quantitycssstyle`, `offerdrawerstatus`, `reservedon`, `reserved`, `ordersynced`, `validqty`, `shippedqty`, `shippedprice`, `countercasepricetotal`, `sortorder`.

**Source rows:** 18,523 (offeritem) / 2,193 (offeritem_offer) / 18,484 (offeritem_device) / 18,014 (offeritem_buyercode)

### pws.order

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pws$order` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| offer_id | bigint | `ecoatm_pws$offer_order` | ecoatm_pws$offerid | FK (via association table, reversed) |
| order_number | varchar | `ecoatm_pws$order` | ordernumber | |
| order_line | varchar | `ecoatm_pws$order` | orderline | |
| order_status | varchar | -- | -- | **NEW in target** (combined status) |
| oracle_status | varchar | `ecoatm_pws$order` | oracleorderstatus | Source is text |
| ship_method | varchar | `ecoatm_pws$order` | shipmethod | |
| shipped_total_qty | integer | `ecoatm_pws$order` | shippedtotalquantity | |
| shipped_total_price | numeric | `ecoatm_pws$order` | shippedtotalprice | Source is integer |
| order_date | timestamp | `ecoatm_pws$order` | orderdate | |
| ship_date | timestamp | `ecoatm_pws$order` | shipdate | |
| oracle_http_code | integer | `ecoatm_pws$order` | oraclehttpcode | |
| created_date | timestamp | `ecoatm_pws$order` | createddate | |
| updated_date | timestamp | `ecoatm_pws$order` | changeddate | |

**Note:** Source has extra columns not migrated: `jsoncontent`, `oraclejsonresponse`, `issuccessful`, `hasshipmentdetails`, `shiptoaddress`, `shippedtotalsku`, `legacyorder`, `deposcoordernumber`.

**Source rows:** 1,612 (order) / 670 (offer_order) / 1,469 (order_buyercode)

### pws.shipment_detail

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pws$shipmentdetail` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| order_id | bigint | `ecoatm_pws$shipmentdetail` | ecoatm_pws$shipmentdetail_order | FK (embedded in source table) |
| tracking_number | varchar | `ecoatm_pws$shipmentdetail` | trackingnumber | |
| tracking_url | varchar | `ecoatm_pws$shipmentdetail` | trackingurl | |
| sku_count | integer | `ecoatm_pws$shipmentdetail` | skucount | |
| quantity | integer | `ecoatm_pws$shipmentdetail` | quantity | |
| created_date | timestamp | `ecoatm_pws$shipmentdetail` | createddate | |
| updated_date | timestamp | `ecoatm_pws$shipmentdetail` | changeddate | |

**Source rows:** 202

---

## Schema: `mdm`

### mdm.brand

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsmdm$brand` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| name | varchar | `ecoatm_pwsmdm$brand` | brand | |
| display_name | varchar | `ecoatm_pwsmdm$brand` | displayname | |
| is_enabled | boolean | `ecoatm_pwsmdm$brand` | isenabledforfilter | |
| sort_rank | integer | `ecoatm_pwsmdm$brand` | rank | |

**Source rows:** 17

### mdm.carrier

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsmdm$carrier` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| name | varchar | `ecoatm_pwsmdm$carrier` | carrier | |
| display_name | varchar | `ecoatm_pwsmdm$carrier` | functionaldevicesdisplayname | Note: source has two display names (functional devices & caselots) |
| is_enabled | boolean | `ecoatm_pwsmdm$carrier` | isenabledforfunctionaldevicesfilter | Note: source has two enable flags |
| sort_rank | integer | `ecoatm_pwsmdm$carrier` | rank | |

**Note:** Source has extra: `isenabledforcaselotsfilter`, `caselotsdisplayname`.

**Source rows:** 32

### mdm.model

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsmdm$model` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| name | varchar | `ecoatm_pwsmdm$model` | model | |
| display_name | varchar | `ecoatm_pwsmdm$model` | displayname | |
| is_enabled | boolean | `ecoatm_pwsmdm$model` | isenabledforfilter | |
| sort_rank | integer | `ecoatm_pwsmdm$model` | rank | |

**Source rows:** 585

### mdm.capacity

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsmdm$capacity` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| name | varchar | `ecoatm_pwsmdm$capacity` | capacity | |
| display_name | varchar | `ecoatm_pwsmdm$capacity` | displayname | |
| is_enabled | boolean | `ecoatm_pwsmdm$capacity` | isenabledforfilter | |
| sort_rank | integer | `ecoatm_pwsmdm$capacity` | rank | |

**Source rows:** 131

### mdm.color

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsmdm$color` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| name | varchar | `ecoatm_pwsmdm$color` | color | |
| display_name | varchar | `ecoatm_pwsmdm$color` | displayname | |
| is_enabled | boolean | `ecoatm_pwsmdm$color` | isenabledforfilter | |
| sort_rank | integer | `ecoatm_pwsmdm$color` | rank | |

**Source rows:** 192

### mdm.condition

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsmdm$condition` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| name | varchar | `ecoatm_pwsmdm$condition` | condition | |
| display_name | varchar | -- | -- | **Not in source** (source has no displayname for condition) |
| is_enabled | boolean | -- | -- | **Not in source** (source has no enable flag for condition) |
| sort_rank | integer | `ecoatm_pwsmdm$condition` | rank | |

**Source rows:** 59

### mdm.category

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsmdm$category` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| name | varchar | `ecoatm_pwsmdm$category` | category | |
| display_name | varchar | `ecoatm_pwsmdm$category` | displayname | |
| is_enabled | boolean | `ecoatm_pwsmdm$category` | isenabledforfilter | |
| sort_rank | integer | `ecoatm_pwsmdm$category` | rank | |

**Source rows:** 3

### mdm.grade

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsmdm$grade` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| name | varchar | `ecoatm_pwsmdm$grade` | grade | |
| display_name | varchar | `ecoatm_pwsmdm$grade` | functionaldevicesdisplayname | Note: source has two display names |
| is_enabled | boolean | `ecoatm_pwsmdm$grade` | isenabledforfunctionaldevicesfilter | Note: source has two enable flags |
| sort_rank | integer | `ecoatm_pwsmdm$grade` | rank | |

**Note:** Source has extra: `isenabledforcaselotsfilter`, `caselotsdisplayname`.

**Source rows:** 8

### mdm.device

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsmdm$device` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| sku | varchar | `ecoatm_pwsmdm$device` | sku | |
| device_code | varchar | `ecoatm_pwsmdm$device` | devicecode | |
| description | varchar | `ecoatm_pwsmdm$device` | devicedescription | |
| list_price | numeric | `ecoatm_pwsmdm$device` | currentlistprice | Source is integer |
| min_price | numeric | `ecoatm_pwsmdm$device` | currentminprice | Source is integer |
| future_list_price | numeric | `ecoatm_pwsmdm$device` | futurelistprice | Source is integer |
| future_min_price | numeric | `ecoatm_pwsmdm$device` | futureminprice | Source is integer |
| available_qty | integer | `ecoatm_pwsmdm$device` | availableqty | |
| reserved_qty | integer | `ecoatm_pwsmdm$device` | reservedqty | |
| atp_qty | integer | `ecoatm_pwsmdm$device` | atpqty | |
| weight | numeric | `ecoatm_pwsmdm$device` | weight | |
| item_type | varchar | `ecoatm_pwsmdm$device` | itemtype | |
| is_active | boolean | `ecoatm_pwsmdm$device` | isactive | |
| brand_id | bigint | `ecoatm_pwsmdm$device_brand` | ecoatm_pwsmdm$brandid | FK (via association table) |
| category_id | bigint | `ecoatm_pwsmdm$device_category` | ecoatm_pwsmdm$categoryid | FK (via association table) |
| model_id | bigint | `ecoatm_pwsmdm$device_model` | ecoatm_pwsmdm$modelid | FK (via association table) |
| condition_id | bigint | `ecoatm_pwsmdm$device_condition` | ecoatm_pwsmdm$conditionid | FK (via association table) |
| capacity_id | bigint | `ecoatm_pwsmdm$device_capacity` | ecoatm_pwsmdm$capacityid | FK (via association table) |
| carrier_id | bigint | `ecoatm_pwsmdm$device_carrier` | ecoatm_pwsmdm$carrierid | FK (via association table) |
| color_id | bigint | `ecoatm_pwsmdm$device_color` | ecoatm_pwsmdm$colorid | FK (via association table) |
| grade_id | bigint | `ecoatm_pwsmdm$device_grade` | ecoatm_pwsmdm$gradeid | FK (via association table) |
| last_sync_time | timestamp | `ecoatm_pwsmdm$device` | lastsynctime | |
| created_date | timestamp | `ecoatm_pwsmdm$device` | createddate | |
| updated_date | timestamp | `ecoatm_pwsmdm$device` | changeddate | |

**Note:** Source has extra columns not migrated: `searchattr`, `modelyear`, `deposcopageno`, `lastupdatedate`.

**Source rows:** 22,476 (device) / ~21K-22K per association table

### mdm.price_history

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsmdm$pricehistory` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| device_id | bigint | `ecoatm_pwsmdm$pricehistory_devicelist` | ecoatm_pwsmdm$deviceid | FK (via association table) |
| list_price | numeric | `ecoatm_pwsmdm$pricehistory` | listprice | Source is integer |
| min_price | numeric | `ecoatm_pwsmdm$pricehistory` | minprice | Source is integer |
| expiration_date | timestamp | `ecoatm_pwsmdm$pricehistory` | expirationdate | |
| created_date | timestamp | `ecoatm_pwsmdm$pricehistory` | createddate | |
| updated_date | timestamp | `ecoatm_pwsmdm$pricehistory` | changeddate | |

**Source rows:** 2,997 (pricehistory) / 2,997 (pricehistory_devicelist)

---

## Schema: `sso`

### sso.sso_configurations

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `saml20$ssoconfiguration` | id | PK |
| alias | varchar | `saml20$ssoconfiguration` | alias | |
| active | boolean | `saml20$ssoconfiguration` | active | |
| is_saml_logging_enabled | boolean | `saml20$ssoconfiguration` | issamlloggingenabled | |
| authn_context | varchar | `saml20$ssoconfiguration` | authncontext | |
| read_idp_metadata_from_url | boolean | `saml20$ssoconfiguration` | readidpmetadatafromurl | |
| create_users | boolean | `saml20$ssoconfiguration` | createusers | |
| allow_idp_initiated_authentication | boolean | `saml20$ssoconfiguration` | allowidpinitiatedauthentication | |
| identifying_assertion_type | varchar | `saml20$ssoconfiguration` | identifyingassertiontype | |
| custom_identifying_assertion_name | text | `saml20$ssoconfiguration` | customidentifyingassertionname | |
| use_custom_logic_for_provisioning | boolean | `saml20$ssoconfiguration` | usecustomlogicforprovisioning | |
| use_custom_after_signin_logic | boolean | `saml20$ssoconfiguration` | usecustomaftersigninlogic | |
| disable_nameid_policy | boolean | `saml20$ssoconfiguration` | disablenameidpolicy | |
| enable_delegated_authentication | boolean | `saml20$ssoconfiguration` | enabledelegatedauthentication | |
| delegated_authentication_url | varchar | `saml20$ssoconfiguration` | delegatedauthenticationurl | |
| enable_mobile_auth_token | boolean | `saml20$ssoconfiguration` | enablemobileauthtoken | |
| response_protocol_binding | varchar | `saml20$ssoconfiguration` | responseprotocolbinding | |
| enable_assertion_consumer_service_index | varchar | `saml20$ssoconfiguration` | enableassertionconsumerserviceindex | |
| assertion_consumer_service_index | integer | `saml20$ssoconfiguration` | assertionconsumerserviceindex | |
| enable_force_authentication | boolean | `saml20$ssoconfiguration` | enableforceauthentication | |
| use_encryption | boolean | `saml20$ssoconfiguration` | useencryption | |
| encryption_method | varchar | `saml20$ssoconfiguration` | encryptionmethod | |
| encryption_key_length | varchar | `saml20$ssoconfiguration` | encryptionkeylength | |
| wizard_mode | boolean | `saml20$ssoconfiguration` | wizardmode | |
| current_wizard_step | varchar | `saml20$ssoconfiguration` | currentwizardstep | |
| claim_maps | jsonb | -- | -- | **NEW in target** (denormalized from saml20$claimmap* tables) |
| idp_metadata_id | bigint | -- | -- | FK (via saml20$ssoconfiguration_idpmetadata association) |
| keystore_id | bigint | -- | -- | FK (via saml20$ssoconfiguration_keystore association) |
| default_role_id | bigint | -- | -- | FK (via saml20$ssoconfiguration_defaultuserroletoassign) |

**Note:** Source has extra: `migratedtoprioritizedsamlauthncontexts`, `isuploadnewkeypair`, `idpmetadataurl`.

**Source rows:** 1

### sso.sp_metadata

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `saml20$spmetadata` | id | PK |
| entity_id | varchar | `saml20$spmetadata` | entityid | |
| organization_name | varchar | `saml20$spmetadata` | organizationname | |
| organization_display_name | varchar | `saml20$spmetadata` | organizationdisplayname | |
| organization_url | varchar | `saml20$spmetadata` | organizationurl | |
| contact_given_name | varchar | `saml20$spmetadata` | contactgivenname | |
| contact_surname | varchar | `saml20$spmetadata` | contactsurname | |
| contact_email_address | varchar | `saml20$spmetadata` | contactemailaddress | |
| application_url | varchar | `saml20$spmetadata` | applicationurl | |
| does_entity_id_differ_from_appurl | boolean | `saml20$spmetadata` | doesentityiddifferfromappurl | |
| log_available_days | integer | `saml20$spmetadata` | logavailabledays | |
| use_encryption | boolean | `saml20$spmetadata` | useencryption | |
| encryption_method | varchar | `saml20$spmetadata` | encryptionmethod | |
| encryption_key_length | varchar | `saml20$spmetadata` | encryptionkeylength | |
| keystore_id | bigint | -- | -- | FK (via saml20$spmetadata_keystore association) |

**Source rows:** 1

### sso.keystores

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `saml20$keystore` | id | PK |
| alias | varchar | `saml20$keystore` | alias | |
| password | varchar | `saml20$keystore` | password | |
| rebuild_keystore | boolean | `saml20$keystore` | rebuildkeystore | |
| last_changed_on | timestamp | `saml20$keystore` | lastchangedon | |

**Source rows:** 0 (empty in qa-0327)

### sso.idp_metadata

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `saml20$idpmetadata` | id | PK |
| entity_id | varchar | -- | -- | **Derived** from saml20$entitydescriptor.entityid via associations |
| idp_metadata_url | varchar | `saml20$ssoconfiguration` | idpmetadataurl | Stored on SSO config in source |
| metadata_xml | text | -- | -- | **NEW in target** |
| updated | boolean | -- | -- | **Derived** from saml20$entitiesdescriptor.updated |
| signing_cert_id | bigint | -- | -- | FK (via saml20$keydescriptor -> keyinfo -> x509certificate) |

**Source rows:** 1

### sso.x509_certificates

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `saml20$x509certificate` | id | PK |
| issuer_name | varchar | `saml20$x509certificate` | issuername | |
| serial_number | text | `saml20$x509certificate` | serialnumber | |
| subject | varchar | `saml20$x509certificate` | subject | |
| valid_from | timestamp | `saml20$x509certificate` | validfrom | |
| valid_until | timestamp | `saml20$x509certificate` | validuntil | |
| base64_cert | text | `saml20$x509certificate` | base64 | |

**Source rows:** 2

### sso.saml_authn_contexts

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `saml20$samlauthncontext` | id | PK |
| description | varchar | `saml20$samlauthncontext` | description | |
| value | varchar | `saml20$samlauthncontext` | value | |
| default_priority | integer | `saml20$samlauthncontext` | defaultpriority | |
| provisioned | boolean | `saml20$samlauthncontext` | provisioned | |
| created_date | timestamp | `saml20$samlauthncontext` | createddate | |
| changed_date | timestamp | `saml20$samlauthncontext` | changeddate | |

**Source rows:** 26

### sso.sso_config_authn_contexts (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| sso_config_id | bigint | `saml20$configuredsamlauthncontext_ssoconfiguration` | saml20$ssoconfigurationid | FK |
| authn_context_id | bigint | `saml20$configuredsamlauthncontext_samlauthncontext` | saml20$samlauthncontextid | FK |
| priority | integer | `saml20$configuredsamlauthncontext` | -- | Priority from configuredsamlauthncontext entity |

**Note:** Source uses a 3-table pattern (configuredsamlauthncontext + two association tables). Target flattens to a junction with priority.

### sso.saml_requests

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `saml20$samlrequest` | id | PK |
| request_id | varchar | `saml20$samlrequest` | requestid | |
| has_request | varchar | `saml20$samlrequest` | hasrequest | |
| has_response | varchar | `saml20$samlrequest` | hasresponse | |
| returned_principal | varchar | `saml20$samlrequest` | returnedprincipal | Email of authenticated user |
| response_id | varchar | `saml20$samlrequest` | responseid | |
| sso_config_id | bigint | `saml20$samlrequest_ssoconfiguration` | saml20$ssoconfigurationid | FK (via association table) |
| saml_response_id | bigint | `saml20$samlrequest_samlresponse` | saml20$samlresponseid | FK (via association table) |

**Source rows:** 9,242

### sso.saml_responses

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `saml20$samlresponse` | id | PK |
| sso_config_id | bigint | `saml20$samlresponse_ssoconfiguration` | saml20$ssoconfigurationid | FK (via association table) |

**Source rows:** 7,973

### sso.sso_audit_log

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `saml20$ssolog` | id | PK |
| message | text | `saml20$ssolog` | message | |
| logon_result | varchar | `saml20$ssolog` | logonresult | |
| created_date | timestamp | `saml20$ssolog` | createddate | |
| changed_date | timestamp | `saml20$ssolog` | changeddate | |
| owner_id | bigint | `saml20$ssolog` | system$owner | |
| changed_by_id | bigint | `saml20$ssolog` | system$changedby | |

**Source rows:** 7,954

### sso.forgot_password_config

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `forgotpassword$configuration` | id | PK |
| deeplink_identifier | varchar | -- | -- | FK via `forgotpassword$configuration_deeplink` association |
| reset_email_template | varchar | -- | -- | FK via `forgotpassword$configuration_emailtemplate_reset` |
| signup_email_template | varchar | -- | -- | FK via `forgotpassword$configuration_emailtemplate_signup` |

**Source rows:** 2

### sso.password_reset_tokens

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `forgotpassword$forgotpassword` | id | PK |
| email_address | varchar | `forgotpassword$forgotpassword` | emailaddress | |
| username | varchar | `forgotpassword$forgotpassword` | username | |
| user_full_name | varchar | `forgotpassword$forgotpassword` | userfullname | |
| token_value | varchar | `forgotpassword$forgotpassword` | forgotpasswordguid | Renamed |
| reset_url | varchar | `forgotpassword$forgotpassword` | forgotpasswordurl | Renamed |
| valid_until | timestamp | `forgotpassword$forgotpassword` | validuntill | Note: source has typo "untill" |
| is_signup | boolean | `forgotpassword$forgotpassword` | issignup | |
| created_date | timestamp | `forgotpassword$forgotpassword` | createddate | |
| account_id | bigint | `forgotpassword$forgotpassword_account` | -- | FK (via association table) |

**Source rows:** 19,231

### sso.password_reset_anon_access (junction)

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| token_id | bigint | `forgotpassword$forgotpassword_anon_user_access` | forgotpassword$forgotpasswordid | FK |
| user_id | bigint | `forgotpassword$forgotpassword_anon_user_access` | system$userid | FK |

---

## Schema: `integration`

### integration.api_log

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsintegration$integration` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| system_target | varchar | -- | -- | **NEW in target** (derived from URL pattern) |
| method | varchar | `ecoatm_pwsintegration$integration` | method | |
| url | text | `ecoatm_pwsintegration$integration` | url | |
| request_payload | text | `ecoatm_pwsintegration$integration` | request | |
| response_payload | text | `ecoatm_pwsintegration$integration` | response | |
| http_status | varchar | `ecoatm_pwsintegration$integration` | responsecode | |
| is_successful | boolean | `ecoatm_pwsintegration$integration` | issuccessful | |
| error_message | text | `ecoatm_pwsintegration$integration` | errormessage | |
| stack_trace | text | `ecoatm_pwsintegration$integration` | stacktrace | |
| start_time | timestamp | `ecoatm_pwsintegration$integration` | starttime | |
| end_time | timestamp | `ecoatm_pwsintegration$integration` | endtime | |
| duration_ms | integer | -- | -- | **Computed** from endtime - starttime |
| created_date | timestamp | `ecoatm_pwsintegration$integration` | createddate | |

**Note:** Source has extra: `errortype`.

**Source rows:** 349,557

### integration.api_token

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsintegration$accesstoken` | id | PK |
| system_target | varchar | -- | -- | **NEW in target** (inferred, e.g. "Deposco") |
| access_token | text | `ecoatm_pwsintegration$accesstoken` | access_token | |
| expiration_date | timestamp | -- | -- | **NEW in target** (source doesn't track expiry) |
| created_date | timestamp | `ecoatm_pwsintegration$accesstoken` | createddate | |

**Source rows:** 2,704,824 (very high -- likely includes historical tokens)

### integration.deposco_config

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsintegration$deposcoconfig` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| base_url | text | `ecoatm_pwsintegration$deposcoconfig` | baseurl | |
| username | varchar | `ecoatm_pwsintegration$deposcoconfig` | username | |
| password_hash | varchar | `ecoatm_pwsintegration$deposcoconfig` | password | Source stores plaintext/encrypted |
| last_sync_time | timestamp | `ecoatm_pwsintegration$deposcoconfig` | lastsynctime | |
| timeout_ms | integer | -- | -- | **NEW in target** (source doesn't have explicit timeout) |
| is_active | boolean | -- | -- | **NEW in target** |
| updated_date | timestamp | `ecoatm_pwsintegration$deposcoconfig` | changeddate | |

**Note:** Source has extra: `teststring`, `pagecount`, `reportattr`.

**Source rows:** 1

### integration.oracle_config

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsintegration$pwsconfiguration` | id | PK |
| legacy_id | bigint | -- | -- | **NEW in target** |
| username | varchar | `ecoatm_pwsintegration$pwsconfiguration` | oracleusername | |
| password_hash | varchar | `ecoatm_pwsintegration$pwsconfiguration` | oraclepassword | Source stores plaintext/encrypted |
| auth_path | text | `ecoatm_pwsintegration$pwsconfiguration` | oracleapipathtoken | |
| create_order_path | text | `ecoatm_pwsintegration$pwsconfiguration` | oracleapipathcreateorder | |
| create_rma_path | text | `ecoatm_pwsintegration$pwsconfiguration` | oracleapipathcreaterma | |
| timeout_ms | integer | `ecoatm_pwsintegration$pwsconfiguration` | oraclehttprequesttimeouttoken | Multiple timeouts in source |
| is_active | boolean | `ecoatm_pwsintegration$pwsconfiguration` | isoraclecreateorderapion | |
| updated_date | timestamp | -- | -- | **NEW in target** |

**Note:** Source has separate timeouts per endpoint: `oraclehttprequesttimeouttoken`, `oraclehttprequesttimeoutcreateorder`, `oraclehttprequesttimeoutcreaterma` and separate active flags: `isoraclecreateorderapion`, `isoraclecreatermaapion`.

**Source rows:** 1

### integration.error_mapping

| Target Column | Type | Source Table | Source Column | Notes |
|--------------|------|-------------|---------------|-------|
| id | bigint | `ecoatm_pwsintegration$pwsresponseconfig` | id | PK |
| source_system | varchar | `ecoatm_pwsintegration$pwsresponseconfig` | sourcesystem | |
| source_error_code | varchar | `ecoatm_pwsintegration$pwsresponseconfig` | sourceerrorcode | |
| source_error_type | varchar | `ecoatm_pwsintegration$pwsresponseconfig` | sourceerrortype | |
| user_error_code | varchar | `ecoatm_pwsintegration$pwsresponseconfig` | usererrorcode | |
| user_error_message | text | `ecoatm_pwsintegration$pwsresponseconfig` | usererrormessage | |
| bypass_for_user | boolean | `ecoatm_pwsintegration$pwsresponseconfig` | bypassforuser | |

**Note:** Source has extra: `sourceerrormessage`.

**Source rows:** 27

---

## Row Count Summary (Source: qa-0327)

### Core Business Data
| Source Table | Rows | Target Table |
|-------------|------|-------------|
| `system$user` | 487 | identity.users |
| `administration$account` | 481 | identity.accounts |
| `system$userrole` | 11 | identity.user_roles |
| `ecoatm_usermanagement$ecoatmdirectuser` | 467 | user_mgmt.ecoatm_direct_users |
| `ecoatm_buyermanagement$buyer` | 579 | buyer_mgmt.buyers |
| `ecoatm_buyermanagement$buyercode` | 653 | buyer_mgmt.buyer_codes |
| `ecoatm_buyermanagement$salesrepresentative` | 3 | buyer_mgmt.sales_representatives |
| `ecoatm_buyermanagement$qualifiedbuyercodes` | 378,755 | buyer_mgmt.qualified_buyer_codes |
| `ecoatm_buyermanagement$auctionsfeature` | 1 | buyer_mgmt.auctions_feature_config |
| `ecoatm_pws$offer` | 1,168 | pws.offer |
| `ecoatm_pws$offeritem` | 18,523 | pws.offer_item |
| `ecoatm_pws$order` | 1,612 | pws.order |
| `ecoatm_pws$shipmentdetail` | 202 | pws.shipment_detail |
| `ecoatm_pwsmdm$device` | 22,476 | mdm.device |
| `ecoatm_pwsmdm$pricehistory` | 2,997 | mdm.price_history |

### MDM Lookup Tables
| Source Table | Rows | Target Table |
|-------------|------|-------------|
| `ecoatm_pwsmdm$brand` | 17 | mdm.brand |
| `ecoatm_pwsmdm$carrier` | 32 | mdm.carrier |
| `ecoatm_pwsmdm$model` | 585 | mdm.model |
| `ecoatm_pwsmdm$capacity` | 131 | mdm.capacity |
| `ecoatm_pwsmdm$color` | 192 | mdm.color |
| `ecoatm_pwsmdm$condition` | 59 | mdm.condition |
| `ecoatm_pwsmdm$category` | 3 | mdm.category |
| `ecoatm_pwsmdm$grade` | 8 | mdm.grade |

### Integration Tables
| Source Table | Rows | Target Table |
|-------------|------|-------------|
| `ecoatm_pwsintegration$integration` | 349,557 | integration.api_log |
| `ecoatm_pwsintegration$accesstoken` | 2,704,824 | integration.api_token |
| `ecoatm_pwsintegration$deposcoconfig` | 1 | integration.deposco_config |
| `ecoatm_pwsintegration$pwsconfiguration` | 1 | integration.oracle_config |
| `ecoatm_pwsintegration$pwsresponseconfig` | 27 | integration.error_mapping |

### SSO Tables
| Source Table | Rows | Target Table |
|-------------|------|-------------|
| `saml20$ssoconfiguration` | 1 | sso.sso_configurations |
| `saml20$spmetadata` | 1 | sso.sp_metadata |
| `saml20$keystore` | 0 | sso.keystores |
| `saml20$idpmetadata` | 1 | sso.idp_metadata |
| `saml20$x509certificate` | 2 | sso.x509_certificates |
| `saml20$samlauthncontext` | 26 | sso.saml_authn_contexts |
| `saml20$samlrequest` | 9,242 | sso.saml_requests |
| `saml20$samlresponse` | 7,973 | sso.saml_responses |
| `saml20$ssolog` | 7,954 | sso.sso_audit_log |
| `forgotpassword$configuration` | 2 | sso.forgot_password_config |
| `forgotpassword$forgotpassword` | 19,231 | sso.password_reset_tokens |

### Reference/System Tables
| Source Table | Rows | Target Table |
|-------------|------|-------------|
| `system$language` | 1 | identity.languages |
| `system$timezone` | 519 | identity.timezones |
| `system$session` | 1 | identity.sessions |

---

## Key Migration Notes

### 1. Mendix Association Tables -> Direct FK Columns
In Mendix, 1:1 and 1:N relationships are stored in separate association tables (e.g., `ecoatm_pws$offer_buyercode`). The target schema **denormalizes** many of these into direct FK columns on the entity table (e.g., `pws.offer.buyer_code_id`). The migration ETL must JOIN the association table to populate the FK.

### 2. Type Conversions
- **Prices:** Source stores as `integer` (cents). Target uses `numeric`. May need division by 100 depending on convention.
- **Statuses:** Source uses Mendix enum strings. Target preserves as `varchar` but may use different enum values.
- **Timestamps:** Both use `timestamp without time zone`. Direct copy.

### 3. New Target-Only Columns
- `legacy_id` on most target tables -- for traceability back to Mendix IDs
- `system_target` on api_log/api_token -- new classification dimension
- `duration_ms` on api_log -- computed from start/end times
- `order_status` on pws.order -- consolidated status field

### 4. Source-Only Columns (Not Migrated)
Many source columns are Mendix UI helpers, validation flags, display-computed fields, or legacy toggles that have no place in the target schema. These are documented in the "Notes" for each table above.

### 5. Cross-Module Foreign Keys
Several buyer_mgmt tables reference `auctionui` entities (bid_round_id, scheduling_auction_id). The auctionui module is **not yet in the target schema**. These FK columns will need to be populated when the auction module is built.

### 6. High-Volume Tables
- `ecoatm_pwsintegration$accesstoken`: 2.7M rows (consider filtering/archiving)
- `ecoatm_pwsintegration$integration` (api_log): 350K rows
- `ecoatm_buyermanagement$qualifiedbuyercodes`: 379K rows
- `forgotpassword$forgotpassword`: 19K rows
