## microsoftgraph

### selectedscopes
- **Table:** `microsoftgraph$selectedscopes` | **Rows:** 4
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$authenticationid | bigint | No | 59391220085948756 |
| microsoftgraph$stringarraywrapperid | bigint | No | 73464968921482988, 73464968921483010, 73464968921483149 |
- **PK:** microsoftgraph$authenticationid, microsoftgraph$stringarraywrapperid

### authentication
- **Table:** `microsoftgraph$authentication` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 59391220085948756 |
| displayname | character varying(200) | Yes | Mendix Prod |
| appid | character varying(200) | Yes | 12abe4fc-65d0-47bc-80c1-dd2... |
| tenant_id | character varying(200) | Yes | 03f2023a-6b70-4bbc-9a20-5bb... |
| authority | character varying(13) | Yes | tenant |
| client_secret | character varying(200) | Yes | {AES3}2n2+S1sUL0v0VeGm;6kNa... |
| prompt | character varying(14) | Yes | none |
| isactive | boolean | Yes | true |
| isdefault | boolean | Yes | true |
| token_endpoint | text | Yes |  |
| jwks_uri | text | Yes |  |
| issuer | text | Yes |  |
| request_uri_parameter_supported | boolean | Yes | false |
| userinfo_endpoint | text | Yes |  |
| authorization_endpoint | text | Yes |  |
| device_authorization_endpoint | text | Yes |  |
| http_logout_supported | boolean | Yes | true |
| frontchannel_logout_supported | boolean | Yes | true |
| end_session_endpoint | text | Yes |  |
| cloud_instance_name | text | Yes |  |
| cloud_graph_host_name | text | Yes |  |
| msgraph_host | text | Yes |  |
| rbac_url | text | Yes |  |
| createddate | timestamp without time zone | Yes | 2024-08-21 15:10:15.358 |
| changeddate | timestamp without time zone | Yes | 2025-02-17 22:15:06.431 |
| system$changedby | bigint | Yes | 23925373020547362 |
| system$owner | bigint | Yes | 23925373020470124 |
- **PK:** id

### authorization
- **Table:** `microsoftgraph$authorization` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 51509920738050180 |
| token_type | text | Yes |  |
| scope | text | Yes |  |
| expires_in | integer | Yes | 3599 |
| access_token | text | Yes |  |
| refresh_token | text | Yes |  |
| id_token | text | Yes |  |
| state | character varying(200) | Yes | c2489ffa-ca33-4225-b92f-fd4... |
| nonce | character varying(200) | Yes |  |
| ext_expires_in | integer | Yes | 3599 |
| successful | boolean | Yes | true |
| adminconsent | boolean | Yes | true |
| grantflow | character varying(18) | Yes | client_credentials |
| createddate | timestamp without time zone | Yes | 2024-08-21 15:18:52.886 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 17:58:24.173 |
| system$changedby | bigint | Yes | 23925373020457621 |
| system$owner | bigint | Yes | 23925373020470124 |
- **PK:** id

### authorization_authentication
- **Table:** `microsoftgraph$authorization_authentication` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$authorizationid | bigint | No | 51509920738050180 |
| microsoftgraph$authenticationid | bigint | No | 59391220085948756 |
- **PK:** microsoftgraph$authorizationid, microsoftgraph$authenticationid

### authorization_user
- **Table:** `microsoftgraph$authorization_user` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$authorizationid | bigint | No | 51509920738050180 |
| system$userid | bigint | No | 23925373020470124 |
- **PK:** microsoftgraph$authorizationid, system$userid

### selectedresponsemode
- **Table:** `microsoftgraph$selectedresponsemode` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$authenticationid | bigint | No | 59391220085948756 |
| microsoftgraph$stringarraywrapperid | bigint | No | 73464968921482066 |
- **PK:** microsoftgraph$authenticationid, microsoftgraph$stringarraywrapperid

### selectedresponsetype
- **Table:** `microsoftgraph$selectedresponsetype` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$authenticationid | bigint | No | 59391220085948756 |
| microsoftgraph$stringarraywrapperid | bigint | No | 73464968921482562 |
- **PK:** microsoftgraph$authenticationid, microsoftgraph$stringarraywrapperid

### claims_supported
- **Table:** `microsoftgraph$claims_supported` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$stringarrayid | bigint | No |  |
| microsoftgraph$authenticationid | bigint | No |  |
- **PK:** microsoftgraph$stringarrayid, microsoftgraph$authenticationid

### deltaquery
- **Table:** `microsoftgraph$deltaquery` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| resource | character varying(21) | Yes |  |
| _odata_deltalink | text | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
- **PK:** id

### id_token_signing_alg_values
- **Table:** `microsoftgraph$id_token_signing_alg_values` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$stringarrayid | bigint | No |  |
| microsoftgraph$authenticationid | bigint | No |  |
- **PK:** microsoftgraph$stringarrayid, microsoftgraph$authenticationid

### profilephoto
- **Table:** `microsoftgraph$profilephoto` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| _id | character varying(200) | Yes |  |
| height | integer | Yes |  |
| width | integer | Yes |  |
| _odata_mediacontenttype | character varying(200) | Yes |  |
- **PK:** id

### profilephoto_user
- **Table:** `microsoftgraph$profilephoto_user` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$profilephotoid | bigint | No |  |
| system$userid | bigint | No |  |
- **PK:** microsoftgraph$profilephotoid, system$userid

### response_modes_supported
- **Table:** `microsoftgraph$response_modes_supported` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$stringarrayid | bigint | No |  |
| microsoftgraph$authenticationid | bigint | No |  |
- **PK:** microsoftgraph$stringarrayid, microsoftgraph$authenticationid

### response_types_supported
- **Table:** `microsoftgraph$response_types_supported` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$stringarrayid | bigint | No |  |
| microsoftgraph$authenticationid | bigint | No |  |
- **PK:** microsoftgraph$stringarrayid, microsoftgraph$authenticationid

### scopes
- **Table:** `microsoftgraph$scopes` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$stringarrayid | bigint | No |  |
| microsoftgraph$authenticationid | bigint | No |  |
- **PK:** microsoftgraph$stringarrayid, microsoftgraph$authenticationid

### stringarray
- **Table:** `microsoftgraph$stringarray` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### stringarraywrapper
- **Table:** `microsoftgraph$stringarraywrapper` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| value | text | Yes |  |
- **PK:** id

### stringarraywrapper_stringarray
- **Table:** `microsoftgraph$stringarraywrapper_stringarray` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$stringarraywrapperid | bigint | No |  |
| microsoftgraph$stringarrayid | bigint | No |  |
- **PK:** microsoftgraph$stringarraywrapperid, microsoftgraph$stringarrayid

### subject_types_supported
- **Table:** `microsoftgraph$subject_types_supported` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$stringarrayid | bigint | No |  |
| microsoftgraph$authenticationid | bigint | No |  |
- **PK:** microsoftgraph$stringarrayid, microsoftgraph$authenticationid

### subscription
- **Table:** `microsoftgraph$subscription` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| _id | character varying(36) | Yes |  |
| changetype | character varying(23) | Yes |  |
| clientstate | text | Yes |  |
| notificationurl | text | Yes |  |
| resource | text | Yes |  |
| expirationdatetime | timestamp without time zone | Yes |  |
| lifecyclenotificationurl | text | Yes |  |
| applicationid | text | Yes |  |
| creatorid | text | Yes |  |
| includeresourcedata | character varying(5) | Yes |  |
| encryptioncertificate | text | Yes |  |
| encryptioncertificateid | text | Yes |  |
| latestsupportedtlsversion | text | Yes |  |
| notificationcontenttype | text | Yes |  |
| notificationqueryoptions | text | Yes |  |
| notificationurlappid | text | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### subscription_authorization
- **Table:** `microsoftgraph$subscription_authorization` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$subscriptionid | bigint | No |  |
| microsoftgraph$authorizationid | bigint | No |  |
- **PK:** microsoftgraph$subscriptionid, microsoftgraph$authorizationid

### subscription_user
- **Table:** `microsoftgraph$subscription_user` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$subscriptionid | bigint | No |  |
| system$userid | bigint | No |  |
- **PK:** microsoftgraph$subscriptionid, system$userid

### token_endpoint_auth_methods_supported
- **Table:** `microsoftgraph$token_endpoint_auth_methods_supported` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$stringarrayid | bigint | No |  |
| microsoftgraph$authenticationid | bigint | No |  |
- **PK:** microsoftgraph$stringarrayid, microsoftgraph$authenticationid

### userinfo
- **Table:** `microsoftgraph$userinfo` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| sub | text | Yes |  |
| name | text | Yes |  |
| family_name | text | Yes |  |
| given_name | text | Yes |  |
| picture | text | Yes |  |
| email | text | Yes |  |
- **PK:** id

### userinfo_authorization
- **Table:** `microsoftgraph$userinfo_authorization` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| microsoftgraph$userinfoid | bigint | No |  |
| microsoftgraph$authorizationid | bigint | No |  |
- **PK:** microsoftgraph$userinfoid, microsoftgraph$authorizationid

---
