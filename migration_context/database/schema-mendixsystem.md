## mendixsystem

### attribute
- **Table:** `mendixsystem$attribute` | **Rows:** 1,939
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | character varying(36) | No | 0014a22b-c76a-4c7a-b41f-c31..., 00237c45-c0b3-421a-837a-4f2..., 0024bb26-adf3-47ac-b516-ce0... |
| entity_id | character varying(255) | No | 4ed29839-1f3c-423a-a289-ded..., f77d3b57-5672-45fb-9aff-2af..., 2489a4dc-f566-42c7-9f77-384... |
| attribute_name | character varying(255) | No | R3POMaxBid, OracleJSONResponse, DisplayPattern |
| column_name | character varying(255) | No | wraptext, modeljson, statusverbiagebidder |
| type | integer | No | 3, 10, 50 |
| length | integer | Yes | 8, 12, 10 |
| default_value | text | Yes |  |
| is_auto_number | boolean | No | true, false |
- **PK:** id

### association
- **Table:** `mendixsystem$association` | **Rows:** 601
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | character varying(36) | No | 005fe2e8-c4f3-4741-8389-bff..., 00640985-3c73-4b15-9705-d4e..., 00647377-9a41-3853-8162-383... |
| association_name | character varying(511) | No | ExcelImporter.Column_Microf..., AuctionUI.AllBidsDocAll_Auc..., ExcelImporter.AdditionalPro... |
| table_name | character varying(255) | No | auctionui$schedulingauction, auctionui$allbiddownload_2_..., ecoatm_pws$order |
| parent_entity_id | character varying(36) | No | 6a4876bd-2c46-478d-b6ba-846..., 26185323-8efb-43dd-872f-33a..., 4ebcbb1f-2a32-4d99-80a0-069... |
| child_entity_id | character varying(36) | No | 26185323-8efb-43dd-872f-33a..., f77d3b57-5672-45fb-9aff-2af..., 4ebcbb1f-2a32-4d99-80a0-069... |
| parent_column_name | character varying(255) | No | taskqueuescheduler$queuedac..., ecoatm_buyermanagement$buyerid, system$userid |
| child_column_name | character varying(255) | No | dataimporter$templateid, ecoatm_buyermanagement$buyerid, system$userid |
| pk_index_name | character varying(255) | Yes |  |
| index_name | character varying(255) | Yes | idx_ecoatm_rma$rmaitem_order, idx_ecoatm_po$weekperiod_pu..., idx_forgotpassword$forgotpa... |
| parent_fkc_name | character varying(255) | Yes |  |
| child_fkc_name | character varying(255) | Yes |  |
| parent_fkc_action | smallint | Yes |  |
| child_fkc_action | smallint | Yes |  |
| storage_format | smallint | Yes | 2, 1 |
- **PK:** id

### entityidentifier
- **Table:** `mendixsystem$entityidentifier` | **Rows:** 595
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | character varying(36) | No | 001a8ae5-6ceb-49a1-b2b5-161..., 001c5bf9-f3ca-4dcb-93fa-aac..., 006132fd-a543-43f9-be43-46c... |
| short_id | smallint | Yes | 537, 354, 75 |
| object_sequence | bigint | Yes | 1498537, 69001, 1671338 |
- **PK:** id

### unique_constraint
- **Table:** `mendixsystem$unique_constraint` | **Rows:** 409
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| name | character varying(255) | No | uniq_aucti$roundthreebuye_r..., uniq_aucti$roundthreebuye_r..., uniq_auctio$bidda_importset... |
| table_id | character varying(36) | No | ac346682-95d0-42df-84cf-42f..., fef5acbd-51d4-474d-9ef2-948..., 0ee07557-b77c-477a-9443-b81... |
| column_id | character varying(36) | No | c7314c4d-a9a4-3b08-8cce-2d4..., 7d9783bf-7041-33f5-a825-193..., 1ee7f935-538a-363e-979b-619... |
- **PK:** name, column_id

### entity
- **Table:** `mendixsystem$entity` | **Rows:** 290
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | character varying(36) | No | 001c5bf9-f3ca-4dcb-93fa-aac..., 006132fd-a543-43f9-be43-46c..., 010fea07-dc34-46b7-9067-47d... |
| entity_name | character varying(511) | No | Administration.Account, AuctionUI.AggInventoryHelper, AuctionUI.AggreegatedInvent... |
| table_name | character varying(255) | No | ecoatm_pwsmdm$grade, auctionui$schedulingauction, ecoatm_pws$order |
| superentity_id | character varying(255) | Yes | 37827192-315d-4ab6-85b8-f62..., c921ccbb-a670-48d9-833d-6a7..., 170ce49d-f29c-4fac-99a6-b55... |
| remote | boolean | Yes | false |
| remote_primary_key | boolean | Yes | false |
- **PK:** id

### index_column
- **Table:** `mendixsystem$index_column` | **Rows:** 273
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| index_id | character varying(36) | No | 009a1755-2c89-3a3e-a0ef-b1c..., 009bada7-2e75-3e4e-99ec-5c6..., 00a9ec1c-4fab-368f-83d4-ffa... |
| column_id | character varying(36) | No | c1e1d01f-860c-384a-884b-bc3..., cf13ec93-aa4e-3daf-a5c7-3f0..., 5cbd663c-a375-313d-8c19-67a... |
| sort_order | boolean | No | true, false |
| ordinal | integer | No | 0, 2, 1 |
- **PK:** index_id, column_id

### index
- **Table:** `mendixsystem$index` | **Rows:** 268
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | character varying(36) | No | 009a1755-2c89-3a3e-a0ef-b1c..., 009bada7-2e75-3e4e-99ec-5c6..., 00a9ec1c-4fab-368f-83d4-ffa... |
| table_id | character varying(36) | No | 26185323-8efb-43dd-872f-33a..., 2489a4dc-f566-42c7-9f77-384..., 4ebcbb1f-2a32-4d99-80a0-069... |
| index_name | character varying(255) | No | idx_excelimporter$column_sy..., idx_xlsreport$mxreferenceha..., idx_ecoatm_buyermanagement$... |
- **PK:** id

### sequence
- **Table:** `mendixsystem$sequence` | **Rows:** 16
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| name | character varying(255) | No | ecoatm_pws$buyeroffer_offer..., xlsreport$mxtemplate_templa..., system$filedocument_fileid_... |
| attribute_id | character varying(36) | No | 18641004-a836-45db-851a-a59..., 0f81688b-e719-4204-8f86-8fc..., 23763768-e4d3-40ca-bb7b-449... |
| start_value | bigint | No | 1001, 1 |
| current_value | bigint | Yes | 1000, 6, 0 |
- **PK:** attribute_id

### properties
- **Table:** `mendixsystem$properties` | **Rows:** 7
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| key | character varying(200) | No | ScheduledEventsExecution, Mendix.Runtime.DeploymentID, DefaultScheduledEventsClean... |
| value | character varying(200) | No | MxAdmin, true, 604800000 |
- **PK:** key

### version
- **Table:** `mendixsystem$version` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| versionnumber | character varying(255) | No | 4.2 |
| lastsyncdate | timestamp without time zone | No | 2026-02-18 21:17:48.571 |
| preanalysismigrationversionnumber | character varying(255) | No | 4.4.0 |
| modelversionnumber | character varying(255) | Yes | unversioned |
| sprintrprojectname | character varying(511) | Yes | Auctions UI |
| mendixversion | character varying(255) | Yes | 10.22.0.68245 |
- **PK:** versionnumber

### remote_primary_key
- **Table:** `mendixsystem$remote_primary_key` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | character varying(36) | No |  |
| entity_id | character varying(255) | No |  |
| attribute_name | character varying(255) | No |  |
| column_name | character varying(255) | No |  |
| type | integer | No |  |
| length | integer | Yes |  |
- **PK:** id

---
