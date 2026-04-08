## saml20

### samlrequest
- **Table:** `saml20$samlrequest` | **Rows:** 297
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 33495522233452783, 33495522233534778, 33495522233496207 |
| requestid | character varying(200) | Yes | 3114d875-166b-4409-9572-99c..., 9d7294b2-8d9e-42f7-97b3-e61..., 7690538f-e0d8-4f09-9529-0c8... |
| hasrequest | character varying(3) | Yes | Yes |
| hasresponse | character varying(3) | Yes | No, Yes |
| returnedprincipal | character varying(200) | Yes | kevin.kim@ecoatm.com, maria.ruvalcaba@ecoatm.com, omar.eldardiry@ecoatm.com |
| responseid | character varying(200) | Yes | _4686a5e8-91ca-47b5-ae68-43..., _6346b8e1-39a0-4217-ac78-fd..., _4cf154a9-84c6-4a52-9642-83... |
- **PK:** id

### samlrequest_ssoconfiguration
- **Table:** `saml20$samlrequest_ssoconfiguration` | **Rows:** 297
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$samlrequestid | bigint | No | 33495522233452783, 33495522233534778, 33495522233496207 |
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
- **PK:** saml20$samlrequestid, saml20$ssoconfigurationid

### samlresponse
- **Table:** `saml20$samlresponse` | **Rows:** 255
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 28147497675764080, 28147497675751676, 28147497675827395 |
- **PK:** id

### samlresponse_ssoconfiguration
- **Table:** `saml20$samlresponse_ssoconfiguration` | **Rows:** 255
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$samlresponseid | bigint | No | 28147497675764080, 28147497675751676, 28147497675827395 |
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
- **PK:** saml20$samlresponseid, saml20$ssoconfigurationid

### samlrequest_samlresponse
- **Table:** `saml20$samlrequest_samlresponse` | **Rows:** 252
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$samlrequestid | bigint | No | 33495522233452783, 33495522233534778, 33495522233496207 |
| saml20$samlresponseid | bigint | No | 28147497675764080, 28147497675751676, 28147497675827395 |
- **PK:** saml20$samlrequestid, saml20$samlresponseid

### ssolog
- **Table:** `saml20$ssolog` | **Rows:** 252
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 42784196464703213, 42784196464781501, 42784196464690983 |
| message | text | Yes |  |
| logonresult | character varying(7) | Yes | Success |
| createddate | timestamp without time zone | Yes | 2026-02-12 17:55:22.552, 2026-02-13 18:37:24.828, 2026-02-13 14:44:49.045 |
| changeddate | timestamp without time zone | Yes | 2026-02-17 15:17:23.177, 2026-02-11 16:42:21.84, 2026-02-12 17:55:22.552 |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### samlauthncontext
- **Table:** `saml20$samlauthncontext` | **Rows:** 26
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 36591746972386608, 36591746972386548, 36591746972388208 |
| description | character varying(200) | Yes | SoftwarePKI, Public Key - XML Digital Si..., Public Key – PGP |
| value | character varying(200) | Yes | urn:oasis:names:tc:SAML:2.0..., urn:oasis:names:tc:SAML:2.0..., urn:oasis:names:tc:SAML:2.0... |
| defaultpriority | integer | Yes | 900, 1000, 400 |
| provisioned | boolean | Yes | true |
| createddate | timestamp without time zone | Yes | 2024-03-28 22:59:13.734, 2024-03-28 22:59:13.712, 2024-03-28 22:59:13.772 |
| changeddate | timestamp without time zone | Yes | 2026-02-20 02:27:18.663, 2026-02-20 02:27:18.646, 2026-02-20 02:27:18.639 |
- **PK:** id

### attribute
- **Table:** `saml20$attribute` | **Rows:** 10
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 25051272927248958, 25051272927261383, 25051272927248652 |
| name | character varying(200) | Yes | urn:oid:0.9.2342.19200300.1..., urn:oid:1.3.6.1.4.1.5923.1...., urn:oid:1.3.6.1.4.1.5923.1.... |
| nameformat | character varying(200) | Yes | urn:oasis:names:tc:SAML:2.0..., string |
| friendlyname | character varying(200) | Yes | displayName, sn, eduPersonTargetedID |
| isrequired | boolean | Yes | false |
| manuallycreated | boolean | Yes | true |
| updated | boolean | Yes | true, false |
| isincommonfederation | boolean | Yes | true, false |
- **PK:** id

### endpoint
- **Table:** `saml20$endpoint` | **Rows:** 3
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 27021597766644623, 27021597766644756, 27021597766644957 |
| binding | character varying(500) | Yes | urn:oasis:names:tc:SAML:2.0..., urn:oasis:names:tc:SAML:2.0... |
| location | character varying(500) | Yes | https://login.microsoftonli... |
| responselocation | character varying(500) | Yes |  |
| index | integer | Yes | 0 |
| isdefault | boolean | Yes | false |
| servicetype | character varying(25) | Yes | SingleLogoutService, SingleSignOnService |
| bindingtype | character varying(29) | Yes | SAML_2_0_HTTP_POST, SAML_2_0_HTTP_Redirect |
- **PK:** id

### endpoint_roledescriptor
- **Table:** `saml20$endpoint_roledescriptor` | **Rows:** 3
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$endpointid | bigint | No | 27021597766644623, 27021597766644756, 27021597766644957 |
| saml20$roledescriptorid | bigint | No | 45880421206257084 |
- **PK:** saml20$endpointid, saml20$roledescriptorid

### attribute_idpmetadata
- **Table:** `saml20$attribute_idpmetadata` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$attributeid | bigint | No | 25051272927261383, 25051272927261549 |
| saml20$idpmetadataid | bigint | No | 31243722414882960 |
- **PK:** saml20$attributeid, saml20$idpmetadataid

### claimmap
- **Table:** `saml20$claimmap` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 25332747903959273, 25332747903971969 |
- **PK:** id

### claimmap_attribute
- **Table:** `saml20$claimmap_attribute` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$claimmapid | bigint | No | 25332747903959273, 25332747903971969 |
| saml20$attributeid | bigint | No | 25051272927261383, 25051272927261549 |
- **PK:** saml20$claimmapid, saml20$attributeid

### claimmap_mxobjectmember
- **Table:** `saml20$claimmap_mxobjectmember` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$claimmapid | bigint | No | 25332747903959273, 25332747903971969 |
| mxmodelreflection$mxobjectmemberid | bigint | No | 16888498602699389, 16888498602699493 |
- **PK:** saml20$claimmapid, mxmodelreflection$mxobjectmemberid

### claimmap_ssoconfiguration
- **Table:** `saml20$claimmap_ssoconfiguration` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$claimmapid | bigint | No | 25332747903959273, 25332747903971969 |
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
- **PK:** saml20$claimmapid, saml20$ssoconfigurationid

### keyinfo
- **Table:** `saml20$keyinfo` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 40813871625466103, 40813871625466131 |
| _id | character varying(200) | Yes |  |
- **PK:** id

### keyinfo_x509certificate
- **Table:** `saml20$keyinfo_x509certificate` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$keyinfoid | bigint | No | 40813871625466103, 40813871625466131 |
| saml20$x509certificateid | bigint | No | 43347146415861947, 43347146415862138 |
- **PK:** saml20$keyinfoid, saml20$x509certificateid

### spattributeconsumingservice
- **Table:** `saml20$spattributeconsumingservice` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 45598946227126521, 45598946227126612 |
| servicename | character varying(200) | Yes | Service1, Service2 |
| lang | character varying(200) | Yes | nl |
| index | integer | Yes | 1, 2 |
| isdefault | boolean | Yes | false, true |
| isactive | boolean | Yes | false |
| logintype | character varying(15) | Yes | Initial_Login, InSession_Login |
- **PK:** id

### spattributeconsumingservice_ssoconfiguration
- **Table:** `saml20$spattributeconsumingservice_ssoconfiguration` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$spattributeconsumingserviceid | bigint | No | 45598946227126521, 45598946227126612 |
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
- **PK:** saml20$spattributeconsumingserviceid, saml20$ssoconfigurationid

### x509certificate
- **Table:** `saml20$x509certificate` | **Rows:** 2
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 43347146415861947, 43347146415862138 |
| issuername | character varying(500) | Yes | CN=Microsoft Azure Federate... |
| serialnumber | text | Yes |  |
| subject | character varying(500) | Yes | CN=Microsoft Azure Federate... |
| validfrom | timestamp without time zone | Yes | 2024-04-05 20:22:42 |
| validuntil | timestamp without time zone | Yes | 2027-04-05 20:22:17 |
| base64 | text | Yes |  |
- **PK:** id

### entitiesdescriptor
- **Table:** `saml20$entitiesdescriptor` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 41939771529887923 |
| validuntil | timestamp without time zone | Yes |  |
| cacheduration | character varying(200) | Yes |  |
| _id | character varying(200) | Yes | _b655bae0-1846-43fc-aa0a-44... |
| name | character varying(200) | Yes |  |
| updated | boolean | Yes | true |
- **PK:** id

### entitiesdescriptor_idpmetadata
- **Table:** `saml20$entitiesdescriptor_idpmetadata` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$entitiesdescriptorid | bigint | No | 41939771529887923 |
| saml20$idpmetadataid | bigint | No | 31243722414882960 |
- **PK:** saml20$entitiesdescriptorid, saml20$idpmetadataid

### entitydescriptor
- **Table:** `saml20$entitydescriptor` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 21955048185851303 |
| entityid | character varying(200) | Yes | https://sts.windows.net/61c... |
| validuntil | timestamp without time zone | Yes |  |
| cacheduration | character varying(200) | Yes |  |
| _id | character varying(200) | Yes | _b655bae0-1846-43fc-aa0a-44... |
| updated | boolean | Yes | true |
- **PK:** id

### entitydescriptor_entitiesdescriptor
- **Table:** `saml20$entitydescriptor_entitiesdescriptor` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$entitydescriptorid | bigint | No | 21955048185851303 |
| saml20$entitiesdescriptorid | bigint | No | 41939771529887923 |
- **PK:** saml20$entitydescriptorid, saml20$entitiesdescriptorid

### idpmetadata
- **Table:** `saml20$idpmetadata` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 31243722414882960 |
- **PK:** id

### keydescriptor
- **Table:** `saml20$keydescriptor` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 37999121858358671 |
| use | character varying(20) | Yes | signing |
- **PK:** id

### keydescriptor_keyinfo
- **Table:** `saml20$keydescriptor_keyinfo` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$keydescriptorid | bigint | No | 37999121858358671 |
| saml20$keyinfoid | bigint | No | 40813871625466131 |
- **PK:** saml20$keydescriptorid, saml20$keyinfoid

### keydescriptor_roledescriptor
- **Table:** `saml20$keydescriptor_roledescriptor` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$keydescriptorid | bigint | No | 37999121858358671 |
| saml20$roledescriptorid | bigint | No | 45880421206257084 |
- **PK:** saml20$keydescriptorid, saml20$roledescriptorid

### keyinfo_entitydescriptor
- **Table:** `saml20$keyinfo_entitydescriptor` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$keyinfoid | bigint | No | 40813871625466103 |
| saml20$entitydescriptorid | bigint | No | 21955048185851303 |
- **PK:** saml20$keyinfoid, saml20$entitydescriptorid

### keystore
- **Table:** `saml20$keystore` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 33776997205278905 |
| lastchangedon | timestamp without time zone | Yes | 2024-05-08 16:20:32.912 |
| alias | character varying(200) | Yes | https://buy.ecoatmdirect.com |
| rebuildkeystore | boolean | Yes | false |
| password | character varying(200) | Yes | {AES3}hfZnp4LYLVziauQC;NGsj... |
- **PK:** id

### roledescriptor
- **Table:** `saml20$roledescriptor` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 45880421206257084 |
| _id | character varying(200) | Yes |  |
| validuntil | timestamp without time zone | Yes |  |
| cacheduration | character varying(200) | Yes |  |
| protocolsupportenumeration | character varying(200) | Yes | urn:oasis:names:tc:SAML:2.0... |
| errorurl | character varying(200) | Yes |  |
| authnrequestssigned | boolean | Yes | false |
| wantauthnrequestssigned | boolean | Yes | false |
| wantassertionssigned | boolean | Yes | false |
| roledescriptortype | character varying(28) | Yes | IDPSSODescriptor |
- **PK:** id

### roledescriptor_entitydescriptor
- **Table:** `saml20$roledescriptor_entitydescriptor` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$roledescriptorid | bigint | No | 45880421206257084 |
| saml20$entitydescriptorid | bigint | No | 21955048185851303 |
- **PK:** saml20$roledescriptorid, saml20$entitydescriptorid

### spmetadata
- **Table:** `saml20$spmetadata` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 24769797950550732 |
| entityid | character varying(200) | Yes | https://buy.ecoatmdirect.com |
| organizationname | character varying(200) | Yes | ecoATM |
| organizationdisplayname | character varying(200) | Yes | ecoATM Auction UI |
| organizationurl | character varying(200) | Yes | https://buy.ecoatmdirect.com |
| contactgivenname | character varying(200) | Yes | Betrand |
| contactsurname | character varying(200) | Yes | Metuge |
| contactemailaddress | character varying(200) | Yes | betrand.metuge@ecoatm.com |
| applicationurl | character varying(200) | Yes | https://ecoatm-auctions.men... |
| doesentityiddifferfromappurl | boolean | Yes | true |
| logavailabledays | integer | Yes | 7 |
| useencryption | boolean | Yes | true |
| encryptionmethod | character varying(13) | Yes | SHA256WithRSA |
| encryptionkeylength | character varying(19) | Yes | _2048bit_Encryption |
- **PK:** id

### ssoconfiguration
- **Table:** `saml20$ssoconfiguration` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 40250921669624027 |
| issamlloggingenabled | boolean | Yes | true |
| authncontext | character varying(7) | Yes | EXACT |
| idpmetadataurl | character varying(200) | Yes | https://login.microsoftonli... |
| readidpmetadatafromurl | boolean | Yes | true |
| wizardmode | boolean | Yes | false |
| createusers | boolean | Yes | false |
| currentwizardstep | character varying(6) | Yes | Step10 |
| alias | character varying(300) | Yes | AzureAD |
| active | boolean | Yes | true |
| allowidpinitiatedauthentication | boolean | Yes | true |
| identifyingassertiontype | character varying(19) | Yes | Use_Name_ID |
| customidentifyingassertionname | text | Yes |  |
| usecustomlogicforprovisioning | boolean | Yes | false |
| usecustomaftersigninlogic | boolean | Yes | true |
| disablenameidpolicy | boolean | Yes | true |
| enabledelegatedauthentication | boolean | Yes | false |
| delegatedauthenticationurl | character varying(500) | Yes |  |
| enablemobileauthtoken | boolean | Yes | false |
| migratedtoprioritizedsamlauthncontexts | boolean | Yes | true |
| responseprotocolbinding | character varying(16) | Yes | POST_BINDING |
| enableassertionconsumerserviceindex | character varying(3) | Yes | No |
| assertionconsumerserviceindex | integer | Yes | 0 |
| enableforceauthentication | boolean | Yes | false |
| useencryption | boolean | Yes | true |
| encryptionmethod | character varying(13) | Yes | SHA256WithRSA |
| encryptionkeylength | character varying(19) | Yes | _2048bit_Encryption |
| isuploadnewkeypair | boolean | Yes | false |
- **PK:** id

### ssoconfiguration_attribute
- **Table:** `saml20$ssoconfiguration_attribute` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
| saml20$attributeid | bigint | No | 25051272927261383 |
- **PK:** saml20$ssoconfigurationid, saml20$attributeid

### ssoconfiguration_customaftersigninmicroflow
- **Table:** `saml20$ssoconfiguration_customaftersigninmicroflow` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
| mxmodelreflection$microflowsid | bigint | No | 15762598695842075 |
- **PK:** saml20$ssoconfigurationid, mxmodelreflection$microflowsid

### ssoconfiguration_defaultuserroletoassign
- **Table:** `saml20$ssoconfiguration_defaultuserroletoassign` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
| system$userroleid | bigint | No | 9570149208162509 |
- **PK:** saml20$ssoconfigurationid, system$userroleid

### ssoconfiguration_idpmetadata
- **Table:** `saml20$ssoconfiguration_idpmetadata` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
| saml20$idpmetadataid | bigint | No | 31243722414882960 |
- **PK:** saml20$ssoconfigurationid, saml20$idpmetadataid

### ssoconfiguration_keystore
- **Table:** `saml20$ssoconfiguration_keystore` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
| saml20$keystoreid | bigint | No | 33776997205278905 |
- **PK:** saml20$ssoconfigurationid, saml20$keystoreid

### ssoconfiguration_mxobjectmember
- **Table:** `saml20$ssoconfiguration_mxobjectmember` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
| mxmodelreflection$mxobjectmemberid | bigint | No | 16888498602697888 |
- **PK:** saml20$ssoconfigurationid, mxmodelreflection$mxobjectmemberid

### ssoconfiguration_mxobjecttype
- **Table:** `saml20$ssoconfiguration_mxobjecttype` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323182487 |
- **PK:** saml20$ssoconfigurationid, mxmodelreflection$mxobjecttypeid

### ssoconfiguration_preferedentitydescriptor
- **Table:** `saml20$ssoconfiguration_preferedentitydescriptor` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No | 40250921669624027 |
| saml20$entitydescriptorid | bigint | No | 21955048185851303 |
- **PK:** saml20$ssoconfigurationid, saml20$entitydescriptorid

### attribute_attributeconsumingservice
- **Table:** `saml20$attribute_attributeconsumingservice` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$attributeid | bigint | No |  |
| saml20$attributeconsumingserviceid | bigint | No |  |
- **PK:** saml20$attributeid, saml20$attributeconsumingserviceid

### attribute_roledescriptor
- **Table:** `saml20$attribute_roledescriptor` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$attributeid | bigint | No |  |
| saml20$roledescriptorid | bigint | No |  |
- **PK:** saml20$attributeid, saml20$roledescriptorid

### attributeconsumingservice
- **Table:** `saml20$attributeconsumingservice` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| index | integer | Yes |  |
| isdefault | boolean | Yes |  |
- **PK:** id

### attributeconsumingservice_roledescriptor
- **Table:** `saml20$attributeconsumingservice_roledescriptor` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$attributeconsumingserviceid | bigint | No |  |
| saml20$roledescriptorid | bigint | No |  |
- **PK:** saml20$attributeconsumingserviceid, saml20$roledescriptorid

### attributeconsumingservicetype_servicedescription
- **Table:** `saml20$attributeconsumingservicetype_servicedescription` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$attributeconsumingserviceid | bigint | No |  |
| saml20$servicepropertyid | bigint | No |  |
- **PK:** saml20$attributeconsumingserviceid, saml20$servicepropertyid

### attributeconsumingservicetype_servicename
- **Table:** `saml20$attributeconsumingservicetype_servicename` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$attributeconsumingserviceid | bigint | No |  |
| saml20$servicepropertyid | bigint | No |  |
- **PK:** saml20$attributeconsumingserviceid, saml20$servicepropertyid

### configuredsamlauthncontext
- **Table:** `saml20$configuredsamlauthncontext` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| priority | integer | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
- **PK:** id

### configuredsamlauthncontext_samlauthncontext
- **Table:** `saml20$configuredsamlauthncontext_samlauthncontext` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$configuredsamlauthncontextid | bigint | No |  |
| saml20$samlauthncontextid | bigint | No |  |
- **PK:** saml20$configuredsamlauthncontextid, saml20$samlauthncontextid

### configuredsamlauthncontext_ssoconfiguration
- **Table:** `saml20$configuredsamlauthncontext_ssoconfiguration` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$configuredsamlauthncontextid | bigint | No |  |
| saml20$ssoconfigurationid | bigint | No |  |
- **PK:** saml20$configuredsamlauthncontextid, saml20$ssoconfigurationid

### contact
- **Table:** `saml20$contact` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| contacttype | character varying(200) | Yes |  |
| company | character varying(200) | Yes |  |
| givenname | character varying(200) | Yes |  |
| surname | character varying(200) | Yes |  |
- **PK:** id

### contactproperty
- **Table:** `saml20$contactproperty` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| _content_ | character varying(200) | Yes |  |
- **PK:** id

### contacttype_emailaddress
- **Table:** `saml20$contacttype_emailaddress` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$contactpropertyid | bigint | No |  |
| saml20$contactid | bigint | No |  |
- **PK:** saml20$contactpropertyid, saml20$contactid

### contacttype_entitydescriptor
- **Table:** `saml20$contacttype_entitydescriptor` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$contactid | bigint | No |  |
| saml20$entitydescriptorid | bigint | No |  |
- **PK:** saml20$contactid, saml20$entitydescriptorid

### contacttype_telephonenumber
- **Table:** `saml20$contacttype_telephonenumber` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$contactpropertyid | bigint | No |  |
| saml20$contactid | bigint | No |  |
- **PK:** saml20$contactpropertyid, saml20$contactid

### entitiesdescriptor_keyinfo
- **Table:** `saml20$entitiesdescriptor_keyinfo` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$entitiesdescriptorid | bigint | No |  |
| saml20$keyinfoid | bigint | No |  |
- **PK:** saml20$entitiesdescriptorid, saml20$keyinfoid

### nameidformat
- **Table:** `saml20$nameidformat` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| description | character varying(200) | Yes |  |
- **PK:** id

### nameidformat_roledescriptor
- **Table:** `saml20$nameidformat_roledescriptor` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$nameidformatid | bigint | No |  |
| saml20$roledescriptorid | bigint | No |  |
- **PK:** saml20$nameidformatid, saml20$roledescriptorid

### organization
- **Table:** `saml20$organization` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### organization_entitydescriptor
- **Table:** `saml20$organization_entitydescriptor` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$organizationid | bigint | No |  |
| saml20$entitydescriptorid | bigint | No |  |
- **PK:** saml20$organizationid, saml20$entitydescriptorid

### organization_organizationdisplayname
- **Table:** `saml20$organization_organizationdisplayname` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$organizationpropertyid | bigint | No |  |
| saml20$organizationid | bigint | No |  |
- **PK:** saml20$organizationpropertyid, saml20$organizationid

### organization_organizationname
- **Table:** `saml20$organization_organizationname` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$organizationpropertyid | bigint | No |  |
| saml20$organizationid | bigint | No |  |
- **PK:** saml20$organizationpropertyid, saml20$organizationid

### organization_organizationurl
- **Table:** `saml20$organization_organizationurl` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$organizationpropertyid | bigint | No |  |
| saml20$organizationid | bigint | No |  |
- **PK:** saml20$organizationpropertyid, saml20$organizationid

### organizationproperty
- **Table:** `saml20$organizationproperty` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| _content_ | character varying(200) | Yes |  |
| lang | character varying(200) | Yes |  |
- **PK:** id

### roledescriptor_organization
- **Table:** `saml20$roledescriptor_organization` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$roledescriptorid | bigint | No |  |
| saml20$organizationid | bigint | No |  |
- **PK:** saml20$roledescriptorid, saml20$organizationid

### roledescriptortype_contactperson
- **Table:** `saml20$roledescriptortype_contactperson` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$contactid | bigint | No |  |
| saml20$roledescriptorid | bigint | No |  |
- **PK:** saml20$contactid, saml20$roledescriptorid

### samlrequest_endpoint
- **Table:** `saml20$samlrequest_endpoint` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$samlrequestid | bigint | No |  |
| saml20$endpointid | bigint | No |  |
- **PK:** saml20$samlrequestid, saml20$endpointid

### serviceproperty
- **Table:** `saml20$serviceproperty` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| _content_ | character varying(200) | Yes |  |
| lang | character varying(200) | Yes |  |
- **PK:** id

### spmetadata_keystore
- **Table:** `saml20$spmetadata_keystore` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$spmetadataid | bigint | No |  |
| saml20$keystoreid | bigint | No |  |
- **PK:** saml20$spmetadataid, saml20$keystoreid

### sprequestedattribute
- **Table:** `saml20$sprequestedattribute` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| name | character varying(200) | Yes |  |
| isrequired | boolean | Yes |  |
| value | character varying(200) | Yes |  |
- **PK:** id

### sprequestedattribute_spattributeconsumingservice
- **Table:** `saml20$sprequestedattribute_spattributeconsumingservice` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$sprequestedattributeid | bigint | No |  |
| saml20$spattributeconsumingserviceid | bigint | No |  |
- **PK:** saml20$sprequestedattributeid, saml20$spattributeconsumingserviceid

### ssoconfigurati_customevaluateinsessionauthenticationmicr
- **Table:** `saml20$ssoconfigurati_customevaluateinsessionauthenticationmicr` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No |  |
| mxmodelreflection$microflowsid | bigint | No |  |
- **PK:** saml20$ssoconfigurationid, mxmodelreflection$microflowsid

### ssoconfiguratio_customprepareinsessionauthenticationmicr
- **Table:** `saml20$ssoconfiguratio_customprepareinsessionauthenticationmicr` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No |  |
| mxmodelreflection$microflowsid | bigint | No |  |
- **PK:** saml20$ssoconfigurationid, mxmodelreflection$microflowsid

### ssoconfiguration_customuserprovisioningmicroflow
- **Table:** `saml20$ssoconfiguration_customuserprovisioningmicroflow` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No |  |
| mxmodelreflection$microflowsid | bigint | No |  |
- **PK:** saml20$ssoconfigurationid, mxmodelreflection$microflowsid

### ssoconfiguration_nameidformat
- **Table:** `saml20$ssoconfiguration_nameidformat` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No |  |
| saml20$nameidformatid | bigint | No |  |
- **PK:** saml20$ssoconfigurationid, saml20$nameidformatid

### ssoconfiguration_samlauthncontext
- **Table:** `saml20$ssoconfiguration_samlauthncontext` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| saml20$ssoconfigurationid | bigint | No |  |
| saml20$samlauthncontextid | bigint | No |  |
- **PK:** saml20$ssoconfigurationid, saml20$samlauthncontextid

---
