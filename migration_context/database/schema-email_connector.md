## email_connector

### emailmessage
- **Table:** `email_connector$emailmessage` | **Rows:** 118,127
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 22799473113563289, 22799473113576098, 22799473113576300 |
| subject | text | Yes |  |
| sentdate | timestamp without time zone | Yes | 2024-05-20 21:35:59.579, 2024-05-20 22:58:21.82, 2024-05-20 22:58:57.863 |
| retrievedate | timestamp without time zone | Yes | 2024-05-20 21:35:28.61, 2024-05-20 22:58:20.921, 2024-05-20 22:58:57.299 |
| from | text | Yes |  |
| to | text | Yes |  |
| cc | text | Yes |  |
| bcc | text | Yes |  |
| content | text | Yes |  |
| useonlyplaintext | boolean | Yes | false |
| hasattachments | boolean | Yes | false |
| size | integer | Yes | 0 |
| fromdisplayname | character varying(200) | Yes | ecoATM Direct |
| replyto | character varying(200) | Yes | wholesalebids@ecoatm.com |
| plainbody | text | Yes |  |
| queuedforsending | boolean | Yes | false |
| resendattempts | integer | Yes | 0 |
| lastsenderror | text | Yes |  |
| lastsendattemptat | timestamp without time zone | Yes | 2025-05-09 02:46:05.088, 2025-06-10 15:01:27.912, 2025-05-12 12:01:22.369 |
| status | character varying(8) | Yes | ERROR, SENT |
| issigned | boolean | Yes | false |
| isencrypted | boolean | Yes | false |
| recipientstoggle | boolean | Yes | false |
| system$owner | bigint | Yes | 281474993317057, 281474990906220, 23925373021481457 |
- **PK:** id

### emailmessage_emailtemplate
- **Table:** `email_connector$emailmessage_emailtemplate` | **Rows:** 116,310
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$emailmessageid | bigint | No | 22799473113601668, 22799473113601883, 22799473113601975 |
| email_connector$emailtemplateid | bigint | No | 46724846134122741, 46724846134109870, 46724846134033042 |
- **PK:** email_connector$emailmessageid, email_connector$emailtemplateid

### emailtemplate_token
- **Table:** `email_connector$emailtemplate_token` | **Rows:** 91
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$emailtemplateid | bigint | No | 46724846134122741, 46724846134109870, 46724846134033042 |
| mxmodelreflection$tokenid | bigint | No | 18295873486436383, 18295873486423531, 18295873486397785 |
- **PK:** email_connector$emailtemplateid, mxmodelreflection$tokenid

### emailtemplate
- **Table:** `email_connector$emailtemplate` | **Rows:** 16
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 46724846134122741, 46724846134109870, 46724846134020321 |
| templatename | character varying(200) | Yes | WelcomeEmailTemplate, PWSRMAApprovalEmail, Inventory_Notification_Remi... |
| creationdate | timestamp without time zone | Yes | 2025-07-10 20:32:54.725, 2024-10-16 19:14:56.768, 2025-05-07 20:56:41.535 |
| subject | character varying(200) | Yes | Bids for Auction {%Auction%..., Bids for Auction {%Auction%..., ecoATM Auction {%Auction%} ... |
| sentdate | timestamp without time zone | Yes |  |
| to | text | Yes |  |
| cc | text | Yes |  |
| bcc | text | Yes |  |
| content | text | Yes |  |
| useonlyplaintext | boolean | Yes | false |
| hasattachment | boolean | Yes | false |
| replyto | character varying(200) | Yes | wholesalebids@ecoatm.com |
| plainbody | text | Yes |  |
| fromdisplayname | character varying(200) | Yes | ecoATM Direct |
| signed | boolean | Yes | false |
| encrypted | boolean | Yes | false |
| recipientstoggle | boolean | Yes | true, false |
| fromaddress | character varying(200) | Yes | wholesalebids@ecoatm.com |
| createddate | timestamp without time zone | Yes | 2024-10-16 19:14:56.768, 2024-11-18 17:12:32.364, 2024-11-13 21:33:02.946 |
| changeddate | timestamp without time zone | Yes | 2025-10-28 20:53:34.952, 2024-09-04 18:41:58.403, 2025-07-17 20:16:12.783 |
| system$changedby | bigint | Yes | 23925373020533909, 23925373020418781, 23925373020431548 |
| system$owner | bigint | Yes | 23925373020533909, 23925373020815588, 281474976710785 |
- **PK:** id

### emailtemplate_mxobjecttype
- **Table:** `email_connector$emailtemplate_mxobjecttype` | **Rows:** 16
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$emailtemplateid | bigint | No | 46724846134122741, 46724846134109870, 46724846134033042 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323208190, 20266198323373057, 20266198323198109 |
- **PK:** email_connector$emailtemplateid, mxmodelreflection$mxobjecttypeid

### emailconnectorlog
- **Table:** `email_connector$emailconnectorlog` | **Rows:** 9
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 27303072740934049, 27303072740933991, 27303072740947109 |
| created | timestamp without time zone | Yes | 2024-05-23 20:10:57.353, 2024-05-24 00:23:56.172, 2024-06-05 09:34:17.204 |
| logtype | character varying(7) | Yes | Error |
| errormessage | text | Yes |  |
| triggeredinmf | character varying(200) | Yes | IVK_CreateAndSendEmail |
| stacktrace | text | Yes |  |
| message | character varying(200) | Yes | Failed to retrieve Email te... |
| isunread | boolean | Yes | false |
- **PK:** id

### attachment
- **Table:** `email_connector$attachment` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| contentid | text | Yes |  |
| attachmentname | text | Yes |  |
| attachmentsize | integer | Yes |  |
| attachmentcontenttype | text | Yes |  |
| position | character varying(10) | Yes |  |
- **PK:** id

### attachment_emailmessage
- **Table:** `email_connector$attachment_emailmessage` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$attachmentid | bigint | No |  |
| email_connector$emailmessageid | bigint | No |  |
- **PK:** email_connector$attachmentid, email_connector$emailmessageid

### attachment_emailtemplate
- **Table:** `email_connector$attachment_emailtemplate` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$attachmentid | bigint | No |  |
| email_connector$emailtemplateid | bigint | No |  |
- **PK:** email_connector$attachmentid, email_connector$emailtemplateid

### emailaccount
- **Table:** `email_connector$emailaccount` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| username | character varying(200) | Yes |  |
| mailaddress | character varying(200) | Yes |  |
| password | text | Yes |  |
| timeout | integer | Yes |  |
| sanitizeemailbodyforxssscript | boolean | Yes |  |
| isp12configured | boolean | Yes |  |
| isldapconfigured | boolean | Yes |  |
| isincomingemailconfigured | boolean | Yes |  |
| isoutgoingemailconfigured | boolean | Yes |  |
| fromdisplayname | character varying(200) | Yes |  |
| usesslcheckserveridentity | boolean | Yes |  |
| issharedmailbox | boolean | Yes |  |
| isoauthused | boolean | Yes |  |
| isemailconfigautodetect | boolean | Yes |  |
| composeemail | boolean | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### emailaccount_ldapconfiguration
- **Table:** `email_connector$emailaccount_ldapconfiguration` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$emailaccountid | bigint | No |  |
| email_connector$ldapconfigurationid | bigint | No |  |
- **PK:** email_connector$emailaccountid, email_connector$ldapconfigurationid

### emailaccount_oauthprovider
- **Table:** `email_connector$emailaccount_oauthprovider` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$emailaccountid | bigint | No |  |
| email_connector$oauthproviderid | bigint | No |  |
- **PK:** email_connector$emailaccountid, email_connector$oauthproviderid

### emailaccount_oauthtoken
- **Table:** `email_connector$emailaccount_oauthtoken` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$emailaccountid | bigint | No |  |
| email_connector$oauthtokenid | bigint | No |  |
- **PK:** email_connector$emailaccountid, email_connector$oauthtokenid

### emailconnectorlog_emailaccount
- **Table:** `email_connector$emailconnectorlog_emailaccount` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$emailconnectorlogid | bigint | No |  |
| email_connector$emailaccountid | bigint | No |  |
- **PK:** email_connector$emailconnectorlogid, email_connector$emailaccountid

### emailconnectorlog_emailmessage
- **Table:** `email_connector$emailconnectorlog_emailmessage` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$emailconnectorlogid | bigint | No |  |
| email_connector$emailmessageid | bigint | No |  |
- **PK:** email_connector$emailconnectorlogid, email_connector$emailmessageid

### emailheader
- **Table:** `email_connector$emailheader` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| key | character varying(200) | Yes |  |
| value | text | Yes |  |
- **PK:** id

### emailheader_emailmessage
- **Table:** `email_connector$emailheader_emailmessage` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$emailheaderid | bigint | No |  |
| email_connector$emailmessageid | bigint | No |  |
- **PK:** email_connector$emailheaderid, email_connector$emailmessageid

### emailmessage_emailaccount
- **Table:** `email_connector$emailmessage_emailaccount` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$emailmessageid | bigint | No |  |
| email_connector$emailaccountid | bigint | No |  |
- **PK:** email_connector$emailmessageid, email_connector$emailaccountid

### incomingemailconfiguration
- **Table:** `email_connector$incomingemailconfiguration` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| incomingprotocol | character varying(5) | Yes |  |
| folder | character varying(200) | Yes |  |
| usebatchimport | boolean | Yes |  |
| batchsize | integer | Yes |  |
| handling | character varying(13) | Yes |  |
| movefolder | character varying(200) | Yes |  |
| processinlineimage | boolean | Yes |  |
| fetchstrategy | character varying(6) | Yes |  |
| notifyonnewemails | boolean | Yes |  |
| serverhost | character varying(200) | Yes |  |
| serverport | integer | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### incomingemailconfiguration_emailaccount
- **Table:** `email_connector$incomingemailconfiguration_emailaccount` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$incomingemailconfigurationid | bigint | No |  |
| email_connector$emailaccountid | bigint | No |  |
- **PK:** email_connector$incomingemailconfigurationid, email_connector$emailaccountid

### ldapconfiguration
- **Table:** `email_connector$ldapconfiguration` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| ldaphost | character varying(200) | Yes |  |
| ldapport | integer | Yes |  |
| ldapusername | character varying(200) | Yes |  |
| ldappassword | character varying(200) | Yes |  |
| isssl | boolean | Yes |  |
| basedn | character varying(200) | Yes |  |
| authtype | character varying(6) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### oauthnonce
- **Table:** `email_connector$oauthnonce` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| state | character varying(200) | Yes |  |
- **PK:** id

### oauthnonce_emailaccount
- **Table:** `email_connector$oauthnonce_emailaccount` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$oauthnonceid | bigint | No |  |
| email_connector$emailaccountid | bigint | No |  |
- **PK:** email_connector$oauthnonceid, email_connector$emailaccountid

### oauthprovider
- **Table:** `email_connector$oauthprovider` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| name | character varying(50) | Yes |  |
| clientid | text | Yes |  |
| clientsecret | text | Yes |  |
| openidwellknownmetadatauri | character varying(700) | Yes |  |
| authorizationendpoint | character varying(700) | Yes |  |
| tokenendpoint | character varying(700) | Yes |  |
| emaildomain | character varying(200) | Yes |  |
| callbackoperationpath | character varying(200) | Yes |  |
| callbackurl | character varying(200) | Yes |  |
| oauthtype | character varying(11) | Yes |  |
| tenantid | character varying(200) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$changedby | bigint | Yes |  |
| system$owner | bigint | Yes |  |
- **PK:** id

### oauthtoken
- **Table:** `email_connector$oauthtoken` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| token_type | character varying(200) | Yes |  |
| scope | text | Yes |  |
| expires_in | integer | Yes |  |
| access_token | text | Yes |  |
| refresh_token | text | Yes |  |
| id_token | text | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### outgoingemailconfiguration
- **Table:** `email_connector$outgoingemailconfiguration` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| outgoingprotocol | character varying(4) | Yes |  |
| ssl | boolean | Yes |  |
| tls | boolean | Yes |  |
| sendmaxattempts | integer | Yes |  |
| serverhost | character varying(200) | Yes |  |
| serverport | integer | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### outgoingemailconfiguration_emailaccount
- **Table:** `email_connector$outgoingemailconfiguration_emailaccount` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$outgoingemailconfigurationid | bigint | No |  |
| email_connector$emailaccountid | bigint | No |  |
- **PK:** email_connector$outgoingemailconfigurationid, email_connector$emailaccountid

### pk12certificate
- **Table:** `email_connector$pk12certificate` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| passphrase | character varying(200) | Yes |  |
- **PK:** id

### pk12certificate_emailaccount
- **Table:** `email_connector$pk12certificate_emailaccount` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$pk12certificateid | bigint | No |  |
| email_connector$emailaccountid | bigint | No |  |
- **PK:** email_connector$pk12certificateid, email_connector$emailaccountid

### querystring
- **Table:** `email_connector$querystring` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| param | character varying(1000) | Yes |  |
- **PK:** id

### querystring_oauthprovider
- **Table:** `email_connector$querystring_oauthprovider` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$querystringid | bigint | No |  |
| email_connector$oauthproviderid | bigint | No |  |
- **PK:** email_connector$querystringid, email_connector$oauthproviderid

### scopeselected
- **Table:** `email_connector$scopeselected` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| scopestring | character varying(1000) | Yes |  |
- **PK:** id

### scopeselected_oauthprovider
- **Table:** `email_connector$scopeselected_oauthprovider` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| email_connector$scopeselectedid | bigint | No |  |
| email_connector$oauthproviderid | bigint | No |  |
- **PK:** email_connector$scopeselectedid, email_connector$oauthproviderid

---
