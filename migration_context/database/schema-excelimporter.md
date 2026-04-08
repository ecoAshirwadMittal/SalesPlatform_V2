## excelimporter

### column
- **Table:** `excelimporter$column` | **Rows:** 55
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 15199648742378459, 15199648742378034, 15199648742376992 |
| colnumber | integer | Yes | 8, 12, 10 |
| text | text | Yes |  |
| mappingtype | character varying(9) | Yes | Attribute, DoNotUse |
| iskey | character varying(3) | Yes | Yes, No |
| isreferencekey | character varying(26) | Yes | NoKey, YesOnlyMainObject |
| status | character varying(7) | Yes | INFO, VALID |
| details | character varying(1000) | Yes | Attribute: Device_Id, type:..., Attribute: Category, type: ..., Attribute: Brand, type: String |
| casesensitive | character varying(3) | Yes | No |
| findattribute | character varying(200) | Yes | TARGET_PRICE, Source_of_inventory, Group |
| findreference | character varying(200) | Yes |  |
| findobjecttype | character varying(200) | Yes |  |
| findmicroflow | character varying(200) | Yes |  |
| datasource | character varying(23) | Yes | CellValue |
| attributetypeenum | character varying(11) | Yes | Decimal, StringType, EnumType |
| inputmask | character varying(20) | Yes |  |
| createddate | timestamp without time zone | Yes | 2024-01-25 15:16:22.585, 2024-06-06 16:37:36.615, 2024-01-25 15:16:22.534 |
| changeddate | timestamp without time zone | Yes | 2024-09-04 18:40:32.235, 2024-09-04 18:40:32.252, 2024-09-04 18:40:32.237 |
| system$changedby | bigint | Yes | 23925373020533909 |
| system$owner | bigint | Yes | 23925373020419277, 281474976710785 |
- **PK:** id

### column_template
- **Table:** `excelimporter$column_template` | **Rows:** 55
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$columnid | bigint | No | 15199648742378459, 15199648742378034, 15199648742376992 |
| excelimporter$templateid | bigint | No | 19703248369771650, 19703248369758929, 19703248369746170 |
- **PK:** excelimporter$columnid, excelimporter$templateid

### xmldocumenttemplate
- **Table:** `excelimporter$xmldocumenttemplate` | **Rows:** 34
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 19421773393086700, 19421773393163909, 19421773393048353 |
- **PK:** id

### column_valuetype
- **Table:** `excelimporter$column_valuetype` | **Rows:** 20
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$columnid | bigint | No | 15199648742375641, 15199648742414205, 15199648742390157 |
| mxmodelreflection$valuetypeid | bigint | No | 14918173765664982, 14918173765665221, 14918173765665108 |
- **PK:** excelimporter$columnid, mxmodelreflection$valuetypeid

### additionalproperties
- **Table:** `excelimporter$additionalproperties` | **Rows:** 3
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 17169973579350227, 17169973579363044, 17169973579375974 |
| printstatisticsmessages | character varying(19) | Yes | AllStatistics |
| printnotfoundmessages_mainobject | boolean | Yes | true |
| ignoreemptykeys | boolean | Yes | true |
| commitunchangedobjects_mainobject | boolean | Yes | true |
| removeunsyncedobjects | character varying(22) | Yes | Nothing |
| resetemptyassociations | boolean | Yes | false |
| createddate | timestamp without time zone | Yes | 2024-01-25 15:15:16.762, 2024-05-20 21:32:26.618, 2024-08-22 03:10:14.594 |
| changeddate | timestamp without time zone | Yes | 2024-01-25 15:22:40.237, 2024-05-20 21:34:28.997, 2024-08-22 03:10:14.594 |
| system$owner | bigint | Yes | 23925373020419277, 281474976710785 |
| system$changedby | bigint | Yes | 23925373020419277, 281474976710785 |
- **PK:** id

### template
- **Table:** `excelimporter$template` | **Rows:** 3
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 19703248369746170, 19703248369758929, 19703248369771650 |
| nr | bigint | Yes | 1, 3, 4 |
| title | character varying(50) | Yes | Auction Inventory Template, Historical Bid Data, Format Bid |
| description | text | Yes |  |
| sheetindex | integer | Yes | 1 |
| headerrownumber | integer | Yes | 1 |
| firstdatarownumber | integer | Yes | 2 |
| status | character varying(7) | Yes | VALID |
| importaction | character varying(23) | Yes | CreateObjects, SynchronizeObjects |
| templatetype | character varying(6) | Yes | Normal |
| createddate | timestamp without time zone | Yes | 2024-01-25 15:15:16.757, 2024-05-20 21:32:26.615, 2024-08-22 03:10:14.525 |
| changeddate | timestamp without time zone | Yes | 2024-09-04 18:40:24.738, 2024-09-04 18:40:32.289, 2024-09-04 18:40:36.213 |
| system$changedby | bigint | Yes | 23925373020533909 |
| system$owner | bigint | Yes | 23925373020419277, 281474976710785 |
- **PK:** id

### template_additionalproperties
- **Table:** `excelimporter$template_additionalproperties` | **Rows:** 3
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$templateid | bigint | No | 19703248369746170, 19703248369758929, 19703248369771650 |
| excelimporter$additionalpropertiesid | bigint | No | 17169973579350227, 17169973579363044, 17169973579375974 |
- **PK:** excelimporter$templateid, excelimporter$additionalpropertiesid

### templatedocument
- **Table:** `excelimporter$templatedocument` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 17732923532771863 |
- **PK:** id

### templatedocument_template
- **Table:** `excelimporter$templatedocument_template` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$templatedocumentid | bigint | No | 17732923532771863 |
| excelimporter$templateid | bigint | No | 19703248369746170 |
- **PK:** excelimporter$templatedocumentid, excelimporter$templateid

### additionalpropertie_mxobjectmember_removeindicato
- **Table:** `excelimporter$additionalpropertie_mxobjectmember_removeindicato` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$additionalpropertiesid | bigint | No |  |
| mxmodelreflection$mxobjectmemberid | bigint | No |  |
- **PK:** excelimporter$additionalpropertiesid, mxmodelreflection$mxobjectmemberid

### column_mastercolumn
- **Table:** `excelimporter$column_mastercolumn` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$columnid1 | bigint | No |  |
| excelimporter$columnid2 | bigint | No |  |
- **PK:** excelimporter$columnid1, excelimporter$columnid2

### column_microflows
- **Table:** `excelimporter$column_microflows` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$columnid | bigint | No |  |
| mxmodelreflection$microflowsid | bigint | No |  |
- **PK:** excelimporter$columnid, mxmodelreflection$microflowsid

### column_mxobjectmember
- **Table:** `excelimporter$column_mxobjectmember` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$columnid | bigint | No |  |
| mxmodelreflection$mxobjectmemberid | bigint | No |  |
- **PK:** excelimporter$columnid, mxmodelreflection$mxobjectmemberid

### column_mxobjectmember_reference
- **Table:** `excelimporter$column_mxobjectmember_reference` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$columnid | bigint | No |  |
| mxmodelreflection$mxobjectmemberid | bigint | No |  |
- **PK:** excelimporter$columnid, mxmodelreflection$mxobjectmemberid

### column_mxobjectreference
- **Table:** `excelimporter$column_mxobjectreference` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$columnid | bigint | No |  |
| mxmodelreflection$mxobjectreferenceid | bigint | No |  |
- **PK:** excelimporter$columnid, mxmodelreflection$mxobjectreferenceid

### column_mxobjecttype
- **Table:** `excelimporter$column_mxobjecttype` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$columnid | bigint | No |  |
| mxmodelreflection$mxobjecttypeid | bigint | No |  |
- **PK:** excelimporter$columnid, mxmodelreflection$mxobjecttypeid

### column_mxobjecttype_reference
- **Table:** `excelimporter$column_mxobjecttype_reference` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$columnid | bigint | No |  |
| mxmodelreflection$mxobjecttypeid | bigint | No |  |
- **PK:** excelimporter$columnid, mxmodelreflection$mxobjecttypeid

### log
- **Table:** `excelimporter$log` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| logline | text | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### log_xmldocumenttemplate
- **Table:** `excelimporter$log_xmldocumenttemplate` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$logid | bigint | No |  |
| excelimporter$xmldocumenttemplateid | bigint | No |  |
- **PK:** excelimporter$logid, excelimporter$xmldocumenttemplateid

### referencehandling
- **Table:** `excelimporter$referencehandling` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| handling | character varying(20) | Yes |  |
| datahandling | character varying(9) | Yes |  |
| printnotfoundmessages | boolean | Yes |  |
| commitunchangedobjects | boolean | Yes |  |
| ignoreemptykeys | boolean | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### referencehandling_mxobjectreference
- **Table:** `excelimporter$referencehandling_mxobjectreference` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$referencehandlingid | bigint | No |  |
| mxmodelreflection$mxobjectreferenceid | bigint | No |  |
- **PK:** excelimporter$referencehandlingid, mxmodelreflection$mxobjectreferenceid

### referencehandling_template
- **Table:** `excelimporter$referencehandling_template` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$referencehandlingid | bigint | No |  |
| excelimporter$templateid | bigint | No |  |
- **PK:** excelimporter$referencehandlingid, excelimporter$templateid

### template_mastertemplate
- **Table:** `excelimporter$template_mastertemplate` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$templateid1 | bigint | No |  |
| excelimporter$templateid2 | bigint | No |  |
- **PK:** excelimporter$templateid1, excelimporter$templateid2

### template_mxobjectreference_parentassociation
- **Table:** `excelimporter$template_mxobjectreference_parentassociation` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$templateid | bigint | No |  |
| mxmodelreflection$mxobjectreferenceid | bigint | No |  |
- **PK:** excelimporter$templateid, mxmodelreflection$mxobjectreferenceid

### template_mxobjecttype
- **Table:** `excelimporter$template_mxobjecttype` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$templateid | bigint | No |  |
| mxmodelreflection$mxobjecttypeid | bigint | No |  |
- **PK:** excelimporter$templateid, mxmodelreflection$mxobjecttypeid

### xmldocumenttemplate_template
- **Table:** `excelimporter$xmldocumenttemplate_template` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| excelimporter$xmldocumenttemplateid | bigint | No |  |
| excelimporter$templateid | bigint | No |  |
- **PK:** excelimporter$xmldocumenttemplateid, excelimporter$templateid

---
