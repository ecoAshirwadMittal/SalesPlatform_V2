## xlsreport

### mxxpath
- **Table:** `xlsreport$mxxpath` | **Rows:** 405
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 48976645948130164, 48976645948052181, 48976645948038360 |
| retrievetype | character varying(9) | Yes | Attribute, Reference |
| subvisible | boolean | Yes | true, false |
- **PK:** id

### columnsettings_mxsheet
- **Table:** `xlsreport$columnsettings_mxsheet` | **Rows:** 334
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxcolumnsettingsid | bigint | No | 65583669573673587, 65583669573608933, 65583669573980130 |
| xlsreport$mxsheetid | bigint | No | 64739244643809492, 64739244643668686, 64739244643451180 |
- **PK:** xlsreport$mxcolumnsettingsid, xlsreport$mxsheetid

### mxcolumnsettings
- **Table:** `xlsreport$mxcolumnsettings` | **Rows:** 334
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 65583669573673587, 65583669573608933, 65583669573980130 |
| columnindex | integer | Yes | 8, 12, 10 |
| autosize | boolean | Yes | true |
| columnwidth | integer | Yes | 0 |
| createddate | timestamp without time zone | Yes | 2026-02-13 03:40:34.63, 2025-01-30 21:35:07.013, 2026-01-07 21:37:26.124 |
| changeddate | timestamp without time zone | Yes | 2025-01-30 21:35:07.013, 2024-05-20 22:16:53.891, 2025-05-21 21:02:45.217 |
| system$owner | bigint | Yes | 23925373020419277, 23925373020815588, 23925373020533909 |
| system$changedby | bigint | Yes | 23925373020419277, 23925373020815588, 23925373020533909 |
- **PK:** id

### mxcolumn
- **Table:** `xlsreport$mxcolumn` | **Rows:** 330
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 63894819713537324, 63894819713781544, 63894819713717394 |
| columnnumber | integer | Yes | 8, 12, 10 |
| objectattribute | character varying(200) | Yes | AuctionUI.ZeroBidDownload /..., EcoATM_PWS.OrderHistoyDownl..., EcoATM_PWS.OfferItem / Coun... |
| dataaggregate | boolean | Yes | false |
| dataaggregatefunction | character varying(7) | Yes |  |
| resultaggregate | boolean | Yes | false |
| resultaggregatefunction | character varying(7) | Yes |  |
- **PK:** id

### mxdata
- **Table:** `xlsreport$mxdata` | **Rows:** 330
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 63894819713537324, 63894819713781544, 63894819713717394 |
| name | character varying(200) | Yes | G_YNN, F_NYN/H_NNN Estimated Quantity, Serial Number |
| status | character varying(3) | Yes | Yes |
| createddate | timestamp without time zone | Yes | 2026-02-13 03:41:04.441, 2026-01-07 21:47:09.653, 2025-10-28 20:32:54.268 |
| changeddate | timestamp without time zone | Yes | 2024-05-20 22:26:27.08, 2025-08-18 20:47:38.651, 2025-04-16 20:41:41.064 |
| submetaobjectname | character varying(255) | Yes | XLSReport.MxColumn |
| system$changedby | bigint | Yes | 23925373020419277, 23925373020815588, 281474976710785 |
| system$owner | bigint | Yes | 23925373020419277, 23925373020815588, 23925373020533909 |
- **PK:** id

### mxdata_mxsheet
- **Table:** `xlsreport$mxdata_mxsheet` | **Rows:** 330
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxdataid | bigint | No | 63894819713537324, 63894819713781544, 63894819713717394 |
| xlsreport$mxsheetid | bigint | No | 64739244643809492, 64739244643668686, 64739244643451180 |
- **PK:** xlsreport$mxdataid, xlsreport$mxsheetid

### mxxpath_mxdata
- **Table:** `xlsreport$mxxpath_mxdata` | **Rows:** 330
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxxpathid | bigint | No | 48976645948130164, 48976645948052181, 48976645948038360 |
| xlsreport$mxdataid | bigint | No | 63894819713537324, 63894819713781544, 63894819713717394 |
- **PK:** xlsreport$mxxpathid, xlsreport$mxdataid

### mxxpath_mxobjectmember
- **Table:** `xlsreport$mxxpath_mxobjectmember` | **Rows:** 327
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxxpathid | bigint | No | 48976645948052181, 48976645948038360, 48976645948130205 |
| mxmodelreflection$mxobjectmemberid | bigint | No | 16888498603328346, 16888498603044697, 16888498603292203 |
- **PK:** xlsreport$mxxpathid, mxmodelreflection$mxobjectmemberid

### mxdata_mxcellstyle
- **Table:** `xlsreport$mxdata_mxcellstyle` | **Rows:** 317
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxdataid | bigint | No | 63894819713652738, 63894819713831671, 63894819713409374 |
| xlsreport$mxcellstyleid | bigint | No | 75153818781975905, 75153818782027383, 75153818782103879 |
- **PK:** xlsreport$mxdataid, xlsreport$mxcellstyleid

### childmxxpath_mxxpath
- **Table:** `xlsreport$childmxxpath_mxxpath` | **Rows:** 71
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxxpathid1 | bigint | No | 48976645948051855, 48976645948102815, 48976645948082019 |
| xlsreport$mxxpathid2 | bigint | No | 48976645948051779, 48976645948082179, 48976645947692897 |
- **PK:** xlsreport$mxxpathid1, xlsreport$mxxpathid2

### mxxpath_mxobjecttype
- **Table:** `xlsreport$mxxpath_mxobjecttype` | **Rows:** 71
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxxpathid | bigint | No | 48976645948051779, 48976645948082179, 48976645947692897 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323334400, 20266198323335186, 20266198323336047 |
- **PK:** xlsreport$mxxpathid, mxmodelreflection$mxobjecttypeid

### mxxpath_parentmxxpath
- **Table:** `xlsreport$mxxpath_parentmxxpath` | **Rows:** 71
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxxpathid1 | bigint | No | 48976645948051779, 48976645948082179, 48976645947692897 |
| xlsreport$mxxpathid2 | bigint | No | 48976645948051855, 48976645948102815, 48976645948082019 |
- **PK:** xlsreport$mxxpathid1, xlsreport$mxxpathid2

### mxreferencehandling
- **Table:** `xlsreport$mxreferencehandling` | **Rows:** 68
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 54324670505464343, 54324670505553659, 54324670505489990 |
| reference | character varying(200) | Yes | AuctionUI.AllBidDownload_2_..., AuctionUI.AllBidDownload_Al..., AuctionUI.BidData_Aggregate... |
| jointype | character varying(5) | Yes | INNER |
- **PK:** id

### mxreferencehandling_mxsheet
- **Table:** `xlsreport$mxreferencehandling_mxsheet` | **Rows:** 68
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxreferencehandlingid | bigint | No | 54324670505464343, 54324670505553659, 54324670505489990 |
| xlsreport$mxsheetid | bigint | No | 64739244643681724, 64739244643476694, 64739244643783918 |
- **PK:** xlsreport$mxreferencehandlingid, xlsreport$mxsheetid

### mxxpath_mxobjectreference
- **Table:** `xlsreport$mxxpath_mxobjectreference` | **Rows:** 66
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxxpathid | bigint | No | 48976645948051779, 48976645948082179, 48976645947692897 |
| mxmodelreflection$mxobjectreferenceid | bigint | No | 14636698789123352, 14636698789124255, 14636698789147239 |
- **PK:** xlsreport$mxxpathid, mxmodelreflection$mxobjectreferenceid

### mxcellstyle
- **Table:** `xlsreport$mxcellstyle` | **Rows:** 53
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 75153818781975905, 75153818781963175, 75153818782103879 |
| name | character varying(200) | Yes | Decimal, Number, Currency |
| textbold | boolean | Yes | false |
| textitalic | boolean | Yes | false |
| textunderline | boolean | Yes | false |
| textalignment | character varying(6) | Yes | Left, Center, Right |
| textverticalalignment | character varying(6) | Yes | Top |
| textcolor | character varying(10) | Yes | Black |
| textheight | integer | Yes | 10 |
| backgroundcolor | character varying(10) | Yes | Blank |
| textrotation | integer | Yes | 0 |
| wraptext | boolean | Yes | true |
| bordertop | integer | Yes | 0, 1 |
| borderbottom | integer | Yes | 0, 1 |
| borderleft | integer | Yes | 0, 1 |
| borderright | integer | Yes | 0, 1 |
| bordercolor | character varying(10) | Yes | Blank, Black |
| format | character varying(8) | Yes | Number, Currency, General |
| decimalplaces | integer | Yes | 2, 0 |
| createddate | timestamp without time zone | Yes | 2025-10-28 20:33:02.887, 2025-05-21 20:46:25.489, 2025-12-03 21:21:36.501 |
| changeddate | timestamp without time zone | Yes | 2025-05-21 21:19:41.093, 2024-05-20 22:15:00.423, 2024-05-20 22:21:28.585 |
| system$changedby | bigint | Yes | 23925373020815588, 23925373020419277, 23925373020533909 |
| system$owner | bigint | Yes | 23925373020815588, 23925373020419277, 23925373020533909 |
| thousandsseparator | boolean | Yes | false |
- **PK:** id

### mxcellstyle_template
- **Table:** `xlsreport$mxcellstyle_template` | **Rows:** 53
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxcellstyleid | bigint | No | 75153818781975905, 75153818781963175, 75153818782103879 |
| xlsreport$mxtemplateid | bigint | No | 57139420272519392, 57139420272532178, 57139420272544995 |
- **PK:** xlsreport$mxcellstyleid, xlsreport$mxtemplateid

### customexcel
- **Table:** `xlsreport$customexcel` | **Rows:** 48
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 54887620459896455, 54887620459541146, 54887620460153072 |
- **PK:** id

### mxsheet
- **Table:** `xlsreport$mxsheet` | **Rows:** 28
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 64739244643681724, 64739244643476694, 64739244643783918 |
| sequence | integer | Yes | 2, 1 |
| name | character varying(200) | Yes | PWSRMADetailsSales, EB Pricing, BidDataRankRound2Export |
| datausage | boolean | Yes | false |
| status | character varying(3) | Yes | Yes |
| distinctdata | boolean | Yes | false |
| startrow | integer | Yes | 2, 1 |
| columnwidthdefault | boolean | Yes | true |
| columnwidthpixels | integer | Yes | 0 |
| rowheightdefault | boolean | Yes | true |
| rowheightpoint | integer | Yes | 0 |
| formlayout_groupby | boolean | Yes | false |
| createddate | timestamp without time zone | Yes | 2025-05-21 21:20:13.095, 2025-12-03 21:21:36.503, 2025-06-26 20:20:57.639 |
| changeddate | timestamp without time zone | Yes | 2024-06-06 16:39:32.366, 2026-02-18 21:25:22.04, 2025-07-10 20:29:44.839 |
| system$changedby | bigint | Yes | 23925373020533909, 23925373020815588, 281474976710785 |
| system$owner | bigint | Yes | 23925373020533909, 23925373020815588, 281474976710785 |
- **PK:** id

### mxsheet_defaultstyle
- **Table:** `xlsreport$mxsheet_defaultstyle` | **Rows:** 28
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxsheetid | bigint | No | 64739244643681724, 64739244643476694, 64739244643783918 |
| xlsreport$mxcellstyleid | bigint | No | 75153818782026974, 75153818782014150, 75153818782039917 |
- **PK:** xlsreport$mxsheetid, xlsreport$mxcellstyleid

### mxsheet_headerstyle
- **Table:** `xlsreport$mxsheet_headerstyle` | **Rows:** 28
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxsheetid | bigint | No | 64739244643681724, 64739244643476694, 64739244643783918 |
| xlsreport$mxcellstyleid | bigint | No | 75153818782026974, 75153818782014150, 75153818782039917 |
- **PK:** xlsreport$mxsheetid, xlsreport$mxcellstyleid

### mxsheet_template
- **Table:** `xlsreport$mxsheet_template` | **Rows:** 28
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxsheetid | bigint | No | 64739244643681724, 64739244643476694, 64739244643783918 |
| xlsreport$mxtemplateid | bigint | No | 57139420272365749, 57139420272608921, 57139420272480960 |
- **PK:** xlsreport$mxsheetid, xlsreport$mxtemplateid

### mxsheet_rowobject
- **Table:** `xlsreport$mxsheet_rowobject` | **Rows:** 27
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxsheetid | bigint | No | 64739244643681724, 64739244643476694, 64739244643783918 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323334940, 20266198323366625, 20266198323475860 |
- **PK:** xlsreport$mxsheetid, mxmodelreflection$mxobjecttypeid

### mxsheet_mxobjectreference
- **Table:** `xlsreport$mxsheet_mxobjectreference` | **Rows:** 26
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxsheetid | bigint | No | 64739244643681724, 64739244643476694, 64739244643681499 |
| mxmodelreflection$mxobjectreferenceid | bigint | No | 14636698789237042, 14636698788996859, 14636698789262060 |
- **PK:** xlsreport$mxsheetid, mxmodelreflection$mxobjectreferenceid

### mxtemplate
- **Table:** `xlsreport$mxtemplate` | **Rows:** 26
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 57139420272608921, 57139420272365749, 57139420272480960 |
| templateid | bigint | Yes | 29, 37, 1 |
| name | character varying(200) | Yes | PWSRMADetailsSales, PWSOrderDetailsByDevice, OfferDrawerSimilarSKU |
| description | text | Yes |  |
| documenttype | character varying(4) | Yes | XLSX, XLS |
| csvseparator | character varying(9) | Yes |  |
| datetimepresentation | character varying(12) | Yes | mmddyyyy, mdyyhmm, mdyy |
| customedateformat | character varying(200) | Yes |  |
| quotationcharacter | character varying(1) | Yes | " |
| createddate | timestamp without time zone | Yes | 2025-05-21 21:16:53.017, 2026-01-07 21:37:26.061, 2025-10-28 20:32:20.897 |
| changeddate | timestamp without time zone | Yes | 2025-10-28 20:33:03.043, 2026-02-13 03:41:11.04, 2025-04-16 22:20:15.845 |
| system$changedby | bigint | Yes | 23925373020815588, 281474976710785, 23925373020547362 |
| system$owner | bigint | Yes | 23925373020533909, 23925373020815588, 281474976710785 |
- **PK:** id

### mxtemplate_inputobject
- **Table:** `xlsreport$mxtemplate_inputobject` | **Rows:** 26
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxtemplateid | bigint | No | 57139420272608921, 57139420272365749, 57139420272480960 |
| mxmodelreflection$mxobjecttypeid | bigint | No | 20266198323385247, 20266198323449714, 20266198323402323 |
- **PK:** xlsreport$mxtemplateid, mxmodelreflection$mxobjecttypeid

### mxtemplate_customexcel
- **Table:** `xlsreport$mxtemplate_customexcel` | **Rows:** 7
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxtemplateid | bigint | No | 57139420272404172, 57139420272365749, 57139420272583334 |
| xlsreport$customexcelid | bigint | No | 54887620459960617, 54887620459947721, 54887620459896455 |
- **PK:** xlsreport$mxtemplateid, xlsreport$customexcelid

### mxconstraint
- **Table:** `xlsreport$mxconstraint` | **Rows:** 4
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 50384020831258826, 50384020831258893, 50384020831271559 |
| sequence | integer | Yes | 1 |
| summary | character varying(200) | Yes | EcoATM_PWS.OrderDataExport ..., EcoATM_PWS.OrderDataExport ... |
| attribute | character varying(200) | Yes | SheetName |
| constraint | character varying(12) | Yes | Equal |
| attributetype | character varying(7) | Yes | Text |
| constrainttext | character varying(50) | Yes | A_YYY, PWS |
| constraintnumber | bigint | Yes | 0 |
| constraintdecimal | numeric | Yes | 0.00000000 |
| constraintdatetime | character varying(6) | Yes |  |
| constraintboolean | boolean | Yes | false |
| andor | character varying(3) | Yes | AND |
- **PK:** id

### mxconstraint_mxsheet
- **Table:** `xlsreport$mxconstraint_mxsheet` | **Rows:** 4
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxconstraintid | bigint | No | 50384020831258826, 50384020831258893, 50384020831271559 |
| xlsreport$mxsheetid | bigint | No | 64739244643783918, 64739244643783960, 64739244643796702 |
- **PK:** xlsreport$mxconstraintid, xlsreport$mxsheetid

### mxconstraint_mxxpath
- **Table:** `xlsreport$mxconstraint_mxxpath` | **Rows:** 4
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxconstraintid | bigint | No | 50384020831258826, 50384020831258893, 50384020831271559 |
| xlsreport$mxxpathid | bigint | No | 48976645948179243, 48976645948181231, 48976645948192106 |
- **PK:** xlsreport$mxconstraintid, xlsreport$mxxpathid

### mxrowsettings
- **Table:** `xlsreport$mxrowsettings` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| rowindex | integer | Yes |  |
| defaultheight | boolean | Yes |  |
| rowheight | integer | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### mxrowsettings_mxsheet
- **Table:** `xlsreport$mxrowsettings_mxsheet` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxrowsettingsid | bigint | No |  |
| xlsreport$mxsheetid | bigint | No |  |
- **PK:** xlsreport$mxrowsettingsid, xlsreport$mxsheetid

### mxsorting
- **Table:** `xlsreport$mxsorting` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| sequence | integer | Yes |  |
| summary | character varying(200) | Yes |  |
| attribute | character varying(200) | Yes |  |
| sortingdirection | character varying(4) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| changeddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
| system$changedby | bigint | Yes |  |
- **PK:** id

### mxsorting_mxsheet
- **Table:** `xlsreport$mxsorting_mxsheet` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxsortingid | bigint | No |  |
| xlsreport$mxsheetid | bigint | No |  |
- **PK:** xlsreport$mxsortingid, xlsreport$mxsheetid

### mxsorting_mxxpath
- **Table:** `xlsreport$mxsorting_mxxpath` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxsortingid | bigint | No |  |
| xlsreport$mxxpathid | bigint | No |  |
- **PK:** xlsreport$mxsortingid, xlsreport$mxxpathid

### mxstatic
- **Table:** `xlsreport$mxstatic` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| columnplace | integer | Yes |  |
| rowplace | integer | Yes |  |
| statictype | character varying(12) | Yes |  |
| aggregatefunction | character varying(7) | Yes |  |
- **PK:** id

### mxstatic_mxcolumn
- **Table:** `xlsreport$mxstatic_mxcolumn` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxstaticid | bigint | No |  |
| xlsreport$mxcolumnid | bigint | No |  |
- **PK:** xlsreport$mxstaticid, xlsreport$mxcolumnid

### mxstatic_mxobjectmember
- **Table:** `xlsreport$mxstatic_mxobjectmember` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| xlsreport$mxstaticid | bigint | No |  |
| mxmodelreflection$mxobjectmemberid | bigint | No |  |
- **PK:** xlsreport$mxstaticid, mxmodelreflection$mxobjectmemberid

---
