## deeplink

### attribute
- **Table:** `deeplink$attribute` | **Rows:** 1,886
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 38280596832791121, 38280596832683156, 38280596832710326 |
| name | character varying(200) | Yes | TextColor, SortAttribute, SortingDirection |
| createddate | timestamp without time zone | Yes | 2024-05-20 21:58:39.648, 2024-05-20 21:58:39.988, 2024-05-20 21:58:38.24 |
| changeddate | timestamp without time zone | Yes | 2024-05-20 21:58:39.988, 2024-05-20 21:58:38.24, 2024-05-20 21:58:37.086 |
| system$owner | bigint | Yes | 281474976710785 |
| system$changedby | bigint | Yes | 281474976710785 |
- **PK:** id

### attribute_entity
- **Table:** `deeplink$attribute_entity` | **Rows:** 1,886
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| deeplink$attributeid | bigint | No | 38280596832791121, 38280596832683156, 38280596832710326 |
| deeplink$entityid | bigint | No | 43910096366897320, 43910096366893660, 43910096366889953 |
- **PK:** deeplink$attributeid, deeplink$entityid

### microflow
- **Table:** `deeplink$microflow` | **Rows:** 671
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 44754521297038517, 44754521297024488, 44754521297008782 |
| name | character varying(200) | Yes | AuctionUI.ACT_CreateInvento..., MxModelReflection.BCo_MxObj..., AuctionUI.ACT_POSTHandsonData |
| friendlyname | character varying(200) | Yes | ACT_SPRequestedAttribute_De..., ACT_GetBuyerCodeHelperList_2, Convert_ChangeType_Import |
| usestringarg | boolean | Yes | true, false |
| useobjectargument | boolean | Yes | true, false |
| module | character varying(200) | Yes | MicrosoftGraph, Encryption, MyFirstModule |
| parameters | text | Yes |  |
| nrofparameters | integer | Yes | 0 |
| createddate | timestamp without time zone | Yes | 2024-05-20 21:58:41.549, 2024-05-20 21:58:40.566, 2024-05-20 21:58:40.772 |
| changeddate | timestamp without time zone | Yes | 2024-05-20 21:58:41.549, 2024-05-20 21:58:40.566, 2024-05-20 21:58:40.772 |
| system$owner | bigint | Yes | 281474976710785 |
| system$changedby | bigint | Yes | 281474976710785 |
- **PK:** id

### param
- **Table:** `deeplink$param` | **Rows:** 529
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| deeplink$microflowid | bigint | No | 44754521297038517, 44754521297008782, 44754521297001469 |
| deeplink$entityid | bigint | No | 43910096366873552, 43910096366867996, 43910096366890227 |
- **PK:** deeplink$microflowid, deeplink$entityid

### entity
- **Table:** `deeplink$entity` | **Rows:** 290
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 43910096366897320, 43910096366893660, 43910096366889953 |
| name | character varying(200) | Yes | Sharepoint.CompletedFileRes..., Encryption.ExampleConfigura..., MicrosoftGraph.NPStringArra... |
| createddate | timestamp without time zone | Yes | 2024-05-20 21:58:38.802, 2024-05-20 21:58:36.911, 2024-05-20 21:58:36.808 |
| changeddate | timestamp without time zone | Yes | 2024-05-20 21:58:38.802, 2024-05-20 21:58:36.911, 2024-05-20 21:58:36.808 |
| system$changedby | bigint | Yes | 281474976710785 |
| system$owner | bigint | Yes | 281474976710785 |
- **PK:** id

### deeplink
- **Table:** `deeplink$deeplink` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 36310271995674839 |
| name | character varying(200) | Yes | ForgotPassword |
| description | text | Yes |  |
| microflow | character varying(200) | Yes | ForgotPassword.Step3_DL_Set... |
| objecttype | character varying(200) | Yes |  |
| objectattribute | character varying(200) | Yes |  |
| allowguests | boolean | Yes | true |
| usestringargument | boolean | Yes | true |
| separategetparameters | boolean | Yes | true |
| useashome | boolean | Yes | false |
| indexpage | character varying(100) | Yes |  |
| hitcount | integer | Yes | 543 |
| argumentexample | character varying(600) | Yes |  |
| useobjectargument | boolean | Yes | false |
| trackhitcount | boolean | Yes | true |
| createddate | timestamp without time zone | Yes | 2024-05-20 21:58:41.574 |
| changeddate | timestamp without time zone | Yes | 2026-02-18 19:31:19.093 |
| system$changedby | bigint | Yes | 281475010309945 |
| system$owner | bigint | Yes | 281474976710785 |
- **PK:** id

### deeplink_language
- **Table:** `deeplink$deeplink_language` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| deeplink$deeplinkid | bigint | No | 36310271995674839 |
| system$languageid | bigint | No | 13792273858822380 |
- **PK:** deeplink$deeplinkid, system$languageid

### deeplink_attribute
- **Table:** `deeplink$deeplink_attribute` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| deeplink$deeplinkid | bigint | No |  |
| deeplink$attributeid | bigint | No |  |
- **PK:** deeplink$deeplinkid, deeplink$attributeid

### deeplink_entity
- **Table:** `deeplink$deeplink_entity` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| deeplink$deeplinkid | bigint | No |  |
| deeplink$entityid | bigint | No |  |
- **PK:** deeplink$deeplinkid, deeplink$entityid

### deeplink_microflow
- **Table:** `deeplink$deeplink_microflow` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| deeplink$deeplinkid | bigint | No |  |
| deeplink$microflowid | bigint | No |  |
- **PK:** deeplink$deeplinkid, deeplink$microflowid

### pendinglink
- **Table:** `deeplink$pendinglink` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| user | character varying(200) | Yes |  |
| argument | bigint | Yes |  |
| stringargument | text | Yes |  |
| sessionid | character varying(50) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### pendinglink_deeplink
- **Table:** `deeplink$pendinglink_deeplink` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| deeplink$pendinglinkid | bigint | No |  |
| deeplink$deeplinkid | bigint | No |  |
- **PK:** deeplink$pendinglinkid, deeplink$deeplinkid

---
