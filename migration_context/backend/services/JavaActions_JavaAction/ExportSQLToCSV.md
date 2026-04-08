# Java Action: ExportSQLToCSV

> This action exports the result of a SQL query to CSV. Upsides: - It is suitable for high volumes because it uses statement cursors to iterate over the data which increases performance and memory consumption (compared to the pagination approach of OQL). Downsides: - No prepared statements - Database specific

**Returns:** `ParameterizedEntity`

## Parameters

| Name | Type | Required |
|---|---|---|
| `statement` | String | ✅ |
| `exportHeaders` | Boolean | ✅ |
| `returnEntity` | Unknown | ✅ |
| `zipResult` | Boolean | ✅ |
| `separator` | String | ✅ |
| `quoteCharacter` | String | ✅ |
| `escapeCharacter` | String | ✅ |
| `characterSet` | String | ✅ |
