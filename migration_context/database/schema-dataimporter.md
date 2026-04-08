## dataimporter

### columnattributemapping
- **Table:** `dataimporter$columnattributemapping` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| columnnumber | integer | Yes |  |
| columnname | text | Yes |  |
| attribute | character varying(255) | Yes |  |
- **PK:** id

### columnattributemapping_csvsheet
- **Table:** `dataimporter$columnattributemapping_csvsheet` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| dataimporter$columnattributemappingid | bigint | No |  |
| dataimporter$csvsheetid | bigint | No |  |
- **PK:** dataimporter$columnattributemappingid, dataimporter$csvsheetid

### columnattributemapping_excelsheet
- **Table:** `dataimporter$columnattributemapping_excelsheet` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| dataimporter$columnattributemappingid | bigint | No |  |
| dataimporter$excelsheetid | bigint | No |  |
- **PK:** dataimporter$columnattributemappingid, dataimporter$excelsheetid

### csvsheet
- **Table:** `dataimporter$csvsheet` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| delimiter | character varying(5) | Yes |  |
| quotecharacter | character varying(5) | Yes |  |
| addheaderrow | boolean | Yes |  |
| escapecharacter | character varying(5) | Yes |  |
- **PK:** id

### csvsheet_template
- **Table:** `dataimporter$csvsheet_template` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| dataimporter$csvsheetid | bigint | No |  |
| dataimporter$templateid | bigint | No |  |
- **PK:** dataimporter$csvsheetid, dataimporter$templateid

### dataimporterelement
- **Table:** `dataimporter$dataimporterelement` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| elementtype | integer | Yes |  |
| primitivetype | character varying(200) | Yes |  |
| path | text | Yes |  |
| decodedpath | text | Yes |  |
| isdefaulttype | boolean | Yes |  |
| minoccurs | integer | Yes |  |
| maxoccurs | integer | Yes |  |
| nillable | boolean | Yes |  |
| exposedname | character varying(255) | Yes |  |
| exposeditemname | character varying(255) | Yes |  |
| maxlength | integer | Yes |  |
| fractiondigits | integer | Yes |  |
| totaldigits | integer | Yes |  |
| errormessage | text | Yes |  |
| warningmessage | text | Yes |  |
| originalvalue | text | Yes |  |
- **PK:** id

### dataimporterelement_csvsheet
- **Table:** `dataimporter$dataimporterelement_csvsheet` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| dataimporter$dataimporterelementid | bigint | No |  |
| dataimporter$csvsheetid | bigint | No |  |
- **PK:** dataimporter$dataimporterelementid, dataimporter$csvsheetid

### dataimporterelement_excelsheet
- **Table:** `dataimporter$dataimporterelement_excelsheet` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| dataimporter$dataimporterelementid | bigint | No |  |
| dataimporter$excelsheetid | bigint | No |  |
- **PK:** dataimporter$dataimporterelementid, dataimporter$excelsheetid

### excelsheet
- **Table:** `dataimporter$excelsheet` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| index | integer | Yes |  |
| sheetname | character varying(31) | Yes |  |
| headerrowstartsat | integer | Yes |  |
| datarowstartsat | integer | Yes |  |
- **PK:** id

### excelsheet_template
- **Table:** `dataimporter$excelsheet_template` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| dataimporter$excelsheetid | bigint | No |  |
| dataimporter$templateid | bigint | No |  |
- **PK:** dataimporter$excelsheetid, dataimporter$templateid

### parent
- **Table:** `dataimporter$parent` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| dataimporter$dataimporterelementid1 | bigint | No |  |
| dataimporter$dataimporterelementid2 | bigint | No |  |
- **PK:** dataimporter$dataimporterelementid1, dataimporter$dataimporterelementid2

### template
- **Table:** `dataimporter$template` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| templatename | character varying(255) | Yes |  |
- **PK:** id

---
