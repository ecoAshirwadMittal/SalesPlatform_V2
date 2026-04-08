## mxmodelreflection

### mxobjectmember
- **Table:** `mxmodelreflection$mxobjectmember` | **Rows:** 3,579
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 16888498602914077, 16888498602900473, 16888498602983119 |
| attributename | character varying(200) | Yes | LDAPPort, QtyFullfiled, BuyerCode |
| attributetype | character varying(200) | Yes | String, HashString, AutoNumber |
| attributetypeenum | character varying(11) | Yes | BooleanType, DateTime, Decimal |
| completename | character varying(400) | Yes | EcoATM_PWS.Offer / FinalOff..., AuctionUI.CarryOverBidsNP /..., Sharepoint.List / Name |
| descriptivename | character varying(200) | Yes |  |
| fieldlength | integer | Yes | 10, 7, 4096 |
| isvirtual | boolean | Yes | true, false |
| createddate | timestamp without time zone | Yes | 2024-08-22 03:13:18.699, 2025-07-10 20:27:26.632, 2024-08-22 03:13:22.875 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 21:19:26.108, 2026-02-18 21:19:38.447, 2026-02-18 21:19:32.977 |
| submetaobjectname | character varying(255) | Yes | MxModelReflection.MxObjectM..., MxModelReflection.MxObjectEnum |
| system$changedby | bigint | Yes | 23925373020815588 |
| system$owner | bigint | Yes | 23925373020815588, 281474976710785, 23925373020547362 |
- **PK:** id

### mxobjectmember_mxobjecttype
- **Table:** `mxmodelreflection$mxobjectmember_mxobjecttype` | **Rows:** 3,579
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$mxobjectmemberid | bigint | No | 16888498602914077, 16888498602900473, 16888498602983119 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323372200, 20266198323311834, 20266198323373302 |
- **PK:** mxmodelreflection$mxobjectmemberid, mxmodelreflection$mxobjecttypeid

### mxobjectmember_type
- **Table:** `mxmodelreflection$mxobjectmember_type` | **Rows:** 3,579
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$mxobjectmemberid | bigint | No | 16888498602914077, 16888498602900473, 16888498602983119 |
| mxmodelreflection$valuetypeid | bigint | No | 14918173765664982, 14918173765677703, 14918173765678066 |
- **PK:** mxmodelreflection$mxobjectmemberid, mxmodelreflection$valuetypeid

### mxobjectreference_mxobjecttype
- **Table:** `mxmodelreflection$mxobjectreference_mxobjecttype` | **Rows:** 2,240
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$mxobjectreferenceid | bigint | No | 14636698789215223, 14636698788975458, 14636698788978044 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323209012, 20266198323182960, 20266198323366625 |
- **PK:** mxmodelreflection$mxobjectreferenceid, mxmodelreflection$mxobjecttypeid

### microflows_inputparameter
- **Table:** `mxmodelreflection$microflows_inputparameter` | **Rows:** 2,165
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$microflowsid | bigint | No | 15762598696051268, 15762598695926947, 15762598696121199 |
| mxmodelreflection$parameterid | bigint | No | 17451448556402687, 17451448556380022, 17451448556166018 |
- **PK:** mxmodelreflection$microflowsid, mxmodelreflection$parameterid

### parameter
- **Table:** `mxmodelreflection$parameter` | **Rows:** 2,165
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 17451448556402687, 17451448556166018, 17451448556380022 |
| name | character varying(200) | Yes | SalesRepresentativeList, Reference, ShowResult |
| createddate | timestamp without time zone | Yes | 2025-02-26 21:23:58.578, 2024-08-22 03:13:31.368, 2025-12-03 21:19:12.83 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 21:19:59.445, 2026-02-18 21:19:55.893, 2026-02-18 21:19:52.32 |
| system$changedby | bigint | Yes | 23925373020815588 |
| system$owner | bigint | Yes | 281474976710785, 23925373020815588, 23925373020547362 |
- **PK:** id

### parameter_valuetype
- **Table:** `mxmodelreflection$parameter_valuetype` | **Rows:** 2,165
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$parameterid | bigint | No | 17451448556402687, 17451448556166018, 17451448556380022 |
| mxmodelreflection$valuetypeid | bigint | No | 14918173765795143, 14918173765882505, 14918173765851392 |
- **PK:** mxmodelreflection$parameterid, mxmodelreflection$valuetypeid

### parameter_mxobjecttype
- **Table:** `mxmodelreflection$parameter_mxobjecttype` | **Rows:** 1,707
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$parameterid | bigint | No | 17451448556402687, 17451448556378286, 17451448556117074 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323373302, 20266198323219429, 20266198323311990 |
- **PK:** mxmodelreflection$parameterid, mxmodelreflection$mxobjecttypeid

### microflows
- **Table:** `mxmodelreflection$microflows` | **Rows:** 1,561
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 15762598696051268, 15762598695926947, 15762598696121199 |
| name | character varying(200) | Yes | ACT_EmailTemplate_Save, ACT_BidDataDoc_ExportExcel_..., Home_ResponsiveProfile |
| module | character varying(200) | Yes | Encryption, CommunityCommons, EcoATM_BuyerManagement |
| completename | character varying(200) | Yes | EcoATM_PWS.SCE_CurrentListP..., EcoATM_PWS.OCH_OfferItemDra..., EcoATM_DA.SUB_CreateEBBidDa... |
| createddate | timestamp without time zone | Yes | 2024-08-22 03:13:31.368, 2025-01-30 21:24:19.657, 2024-08-22 03:13:31.593 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 21:20:00.994, 2026-02-18 21:19:56.691, 2026-02-18 21:19:53.154 |
| system$changedby | bigint | Yes | 23925373020815588 |
| system$owner | bigint | Yes | 281474976710785, 23925373020815588, 23925373020547362 |
- **PK:** id

### microflows_module
- **Table:** `mxmodelreflection$microflows_module` | **Rows:** 1,561
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$microflowsid | bigint | No | 15762598696051268, 15762598695926947, 15762598696121199 |
| mxmodelreflection$moduleid | bigint | No | 19140298416401565, 19140298416338196, 19140298416337743 |
- **PK:** mxmodelreflection$microflowsid, mxmodelreflection$moduleid

### microflows_output_type
- **Table:** `mxmodelreflection$microflows_output_type` | **Rows:** 1,561
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$microflowsid | bigint | No | 15762598696051268, 15762598695926947, 15762598696121199 |
| mxmodelreflection$valuetypeid | bigint | No | 14918173765845142, 14918173765684483, 14918173765895333 |
- **PK:** mxmodelreflection$microflowsid, mxmodelreflection$valuetypeid

### mxobjectreference_mxobjecttype_child
- **Table:** `mxmodelreflection$mxobjectreference_mxobjecttype_child` | **Rows:** 1,360
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$mxobjectreferenceid | bigint | No | 14636698789215223, 14636698788975458, 14636698788978044 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323219429, 20266198323311990, 20266198323334781 |
- **PK:** mxmodelreflection$mxobjectreferenceid, mxmodelreflection$mxobjecttypeid

### captions
- **Table:** `mxmodelreflection$captions` | **Rows:** 1,016
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$mxobjectenumvalueid | bigint | No | 16325548649234329, 16325548649240327, 16325548649311311 |
| mxmodelreflection$mxobjectenumcaptionsid | bigint | No | 18858823439831822, 18858823439696407, 18858823439661412 |
- **PK:** mxmodelreflection$mxobjectenumvalueid, mxmodelreflection$mxobjectenumcaptionsid

### mxobjectenumcaptions
- **Table:** `mxmodelreflection$mxobjectenumcaptions` | **Rows:** 1,016
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 18858823439831822, 18858823439661412, 18858823439696407 |
| caption | character varying(200) | Yes | Reference, Greater, Step7 |
| languagecode | character varying(8) | Yes | en_US |
| languagename | character varying(200) | Yes |  |
| createddate | timestamp without time zone | Yes | 2024-05-20 21:38:41.827, 2024-11-13 21:25:49.123, 2024-05-20 21:38:41.828 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 21:19:30.991, 2026-02-18 21:19:36.581, 2026-02-18 21:19:34.272 |
| system$owner | bigint | Yes | 281474976710785, 23925373020815588, 23925373020547362 |
| system$changedby | bigint | Yes | 23925373020419277, 281474976710785, 23925373020815588 |
- **PK:** id

### mxobjectenumvalue
- **Table:** `mxmodelreflection$mxobjectenumvalue` | **Rows:** 1,016
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 16325548649234329, 16325548649240327, 16325548649311311 |
| name | character varying(200) | Yes | Reference, Greater, Step7 |
| createddate | timestamp without time zone | Yes | 2024-05-20 21:38:41.827, 2024-11-13 21:25:49.123, 2024-05-20 21:38:41.828 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 21:19:32.027, 2026-02-18 21:19:37.926, 2026-02-18 21:19:36.297 |
| system$owner | bigint | Yes | 281474976710785, 23925373020815588, 23925373020547362 |
| system$changedby | bigint | Yes | 23925373020419277, 281474976710785, 23925373020815588 |
- **PK:** id

### mxobjectreference_mxobjecttype_parent
- **Table:** `mxmodelreflection$mxobjectreference_mxobjecttype_parent` | **Rows:** 912
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$mxobjectreferenceid | bigint | No | 14636698789215223, 14636698788975458, 14636698788978044 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323311834, 20266198323373302, 20266198323219429 |
- **PK:** mxmodelreflection$mxobjectreferenceid, mxmodelreflection$mxobjecttypeid

### values
- **Table:** `mxmodelreflection$values` | **Rows:** 888
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$mxobjectenumid | bigint | No | 16044073672738922, 16044073672712927, 16044073672572555 |
| mxmodelreflection$mxobjectenumvalueid | bigint | No | 16325548649269630, 16325548649234329, 16325548649240327 |
- **PK:** mxmodelreflection$mxobjectenumid, mxmodelreflection$mxobjectenumvalueid

### mxobjectreference
- **Table:** `mxmodelreflection$mxobjectreference` | **Rows:** 784
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 14636698789215223, 14636698788975458, 14636698788978044 |
| completename | character varying(200) | Yes | System.WorkflowCurrentActiv..., DatabaseConnector.Parameter..., AuctionUI.SchedulingAuction... |
| module | character varying(200) | Yes | Encryption, EcoATM_BuyerManagement, XLSReport |
| name | character varying(200) | Yes | FacilityInventoryItem_Test_..., ConfiguredSAMLAuthnContext_..., BuyerBidDetailReportHelperO... |
| readablename | character varying(200) | Yes | System.WorkflowCurrentActiv..., DatabaseConnector.Parameter..., AuctionUI.SchedulingAuction... |
| referencetype | character varying(12) | Yes | ReferenceSet, Reference |
| associationowner | character varying(8) | Yes | Both, _Default |
| parententity | character varying(200) | Yes | Email_Connector.OAuthToken, Sharepoint.Folder, EcoATM_Integration.UpsertBu... |
| createddate | timestamp without time zone | Yes | 2024-05-20 21:38:47.199, 2024-11-13 21:26:07.276, 2025-07-10 20:27:34.579 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 21:19:49.562, 2026-02-18 21:19:43.872, 2026-02-18 21:19:43.871 |
| system$owner | bigint | Yes | 281474976710785, 23925373020815588, 23925373020547362 |
| system$changedby | bigint | Yes | 23925373020815588 |
- **PK:** id

### mxobjectreference_module
- **Table:** `mxmodelreflection$mxobjectreference_module` | **Rows:** 784
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$mxobjectreferenceid | bigint | No | 14636698789215223, 14636698788975458, 14636698788978044 |
| mxmodelreflection$moduleid | bigint | No | 19140298416401565, 19140298416338196, 19140298416337743 |
- **PK:** mxmodelreflection$mxobjectreferenceid, mxmodelreflection$moduleid

### mxobjecttype
- **Table:** `mxmodelreflection$mxobjecttype` | **Rows:** 549
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 20266198323372200, 20266198323311834, 20266198323373302 |
| completename | character varying(200) | Yes | SnowflakeRESTSQL.ResultSet, EcoATM_Integration.UpsertBu..., EcoATM_RMA.RMAItem |
| name | character varying(200) | Yes | ResultSet_AUCTIONS_GENERATE..., AuthorizationResponse, AuctionsFeature |
| module | character varying(200) | Yes | Encryption, CommunityCommons, EcoATM_BuyerManagement |
| readablename | character varying(400) | Yes | BuyercodeItem from the EcoA..., Environment from the Custom..., QueryResult from the EcoATM... |
| persistencetype | character varying(14) | Yes | Persistable, Non_persistent |
| createddate | timestamp without time zone | Yes | 2024-03-28 23:01:23.594, 2024-05-20 21:37:52.207, 2024-03-28 23:01:23.852 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 21:19:43.589, 2026-02-18 21:19:43.194, 2026-02-18 21:19:47.524 |
| system$owner | bigint | Yes | 281474976710785, 23925373020815588, 23925373020547362 |
| system$changedby | bigint | Yes | 23925373020815588 |
- **PK:** id

### mxobjecttype_module
- **Table:** `mxmodelreflection$mxobjecttype_module` | **Rows:** 549
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323372200, 20266198323311834, 20266198323373302 |
| mxmodelreflection$moduleid | bigint | No | 19140298416401565, 19140298416338196, 19140298416337743 |
- **PK:** mxmodelreflection$mxobjecttypeid, mxmodelreflection$moduleid

### valuetype
- **Table:** `mxmodelreflection$valuetype` | **Rows:** 390
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 14918173765795143, 14918173765882505, 14918173765946557 |
| name | character varying(200) | Yes | List of type: EcoATM_PO.POD..., List of type: EcoATM_RMA.RM..., Object of type: Email_Conne... |
| typeenum | character varying(11) | Yes | BooleanType, DateTime, ObjectList |
| createddate | timestamp without time zone | Yes | 2024-08-22 03:13:31.165, 2024-03-28 23:01:34.398, 2024-03-28 23:01:34.323 |
| changeddate | timestamp without time zone | Yes | 2025-08-18 20:27:24.788, 2024-08-22 03:13:31.165, 2024-03-28 23:01:34.398 |
| system$changedby | bigint | Yes | 23925373020815588, 281474976710785, 23925373020547362 |
| system$owner | bigint | Yes | 23925373020815588, 281474976710785, 23925373020547362 |
- **PK:** id

### valuetype_mxobjecttype
- **Table:** `mxmodelreflection$valuetype_mxobjecttype` | **Rows:** 352
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$valuetypeid | bigint | No | 14918173765795143, 14918173765882505, 14918173765946557 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323311834, 20266198323373302, 20266198323219429 |
- **PK:** mxmodelreflection$valuetypeid, mxmodelreflection$mxobjecttypeid

### mxobjectenum
- **Table:** `mxmodelreflection$mxobjectenum` | **Rows:** 191
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 16044073672738922, 16044073672712927, 16044073672572555 |
- **PK:** id

### token
- **Table:** `mxmodelreflection$token` | **Rows:** 91
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 18295873486436383, 18295873486423531, 18295873486397785 |
| token | character varying(50) | Yes | BidUrl, ConcatBuyerCodes, ResetURL |
| prefix | character varying(3) | Yes | {% |
| suffix | character varying(3) | Yes | %} |
| combinedtoken | character varying(56) | Yes | {%Note%}, {%Auction%}, {%BidSheet_Url%} |
| description | character varying(300) | Yes | BidUrl, ConcatBuyerCodes, RMAButtonHTML |
| metamodelpath | character varying(1000) | Yes | AuctionUI.EmailNotification..., ForgotPassword.ForgotPasswo..., AuctionUI.EmailNotification... |
| tokentype | character varying(9) | Yes | Attribute |
| status | character varying(7) | Yes | Valid |
| findobjectstart | character varying(200) | Yes |  |
| findobjectreference | character varying(200) | Yes |  |
| findreference | character varying(200) | Yes |  |
| findmember | character varying(200) | Yes | Name, DateTime, ForgotPasswordURL |
| findmemberreference | character varying(200) | Yes | BidSheet_Url, ConcatBuyerCodes, Name |
| isoptional | boolean | Yes | true, false |
| displaypattern | character varying(50) | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-05-07 21:09:21.761, 2025-07-17 20:16:24.97, 2025-07-10 20:36:04.617 |
| changeddate | timestamp without time zone | Yes | 2024-11-13 21:41:12.49, 2025-03-03 16:43:11.125, 2025-03-10 11:59:26.18 |
| system$changedby | bigint | Yes | 23925373020533909, 23925373020777100, 23925373020418781 |
| system$owner | bigint | Yes | 23925373020815588, 281474976710785, 23925373020533909 |
- **PK:** id

### token_mxobjectmember
- **Table:** `mxmodelreflection$token_mxobjectmember` | **Rows:** 91
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$tokenid | bigint | No | 18295873486436383, 18295873486423531, 18295873486397785 |
| mxmodelreflection$mxobjectmemberid | bigint | No | 16888498603171196, 16888498603170631, 16888498602751680 |
- **PK:** mxmodelreflection$tokenid, mxmodelreflection$mxobjectmemberid

### token_mxobjecttype_start
- **Table:** `mxmodelreflection$token_mxobjecttype_start` | **Rows:** 91
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$tokenid | bigint | No | 18295873486436383, 18295873486423531, 18295873486397785 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323373057, 20266198323198109, 20266198323208190 |
- **PK:** mxmodelreflection$tokenid, mxmodelreflection$mxobjecttypeid

### mxobjecttype_subclassof_mxobjecttype
- **Table:** `mxmodelreflection$mxobjecttype_subclassof_mxobjecttype` | **Rows:** 78
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$mxobjecttypeid1 | bigint | No | 20266198323189341, 20266198323264454, 20266198323373302 |
| mxmodelreflection$mxobjecttypeid2 | bigint | No | 20266198323182487, 20266198323185326, 20266198323260803 |
- **PK:** mxmodelreflection$mxobjecttypeid1, mxmodelreflection$mxobjecttypeid2

### module
- **Table:** `mxmodelreflection$module` | **Rows:** 47
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 19140298416401565, 19140298416338196, 19140298416440156 |
| modulename | character varying(200) | Yes | Encryption, EcoATM_BuyerManagement, CommunityCommons |
| synchronizeobjectswithinmodule | boolean | Yes | true, false |
- **PK:** id

### dbsizeestimate
- **Table:** `mxmodelreflection$dbsizeestimate` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| nrofrecords | integer | Yes |  |
| calculatedsizeinbytes | bigint | Yes |  |
| calculatedsizeinkilobytes | bigint | Yes |  |
| findobjecttype | character varying(200) | Yes |  |
- **PK:** id

### dbsizeestimate_mxobjecttype
- **Table:** `mxmodelreflection$dbsizeestimate_mxobjecttype` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$dbsizeestimateid | bigint | No |  |
| mxmodelreflection$mxobjecttypeid | bigint | No |  |
- **PK:** mxmodelreflection$dbsizeestimateid, mxmodelreflection$mxobjecttypeid

### token_mxobjectreference
- **Table:** `mxmodelreflection$token_mxobjectreference` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$tokenid | bigint | No |  |
| mxmodelreflection$mxobjectreferenceid | bigint | No |  |
- **PK:** mxmodelreflection$tokenid, mxmodelreflection$mxobjectreferenceid

### token_mxobjecttype_referenced
- **Table:** `mxmodelreflection$token_mxobjecttype_referenced` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| mxmodelreflection$tokenid | bigint | No |  |
| mxmodelreflection$mxobjecttypeid | bigint | No |  |
- **PK:** mxmodelreflection$tokenid, mxmodelreflection$mxobjecttypeid

---
