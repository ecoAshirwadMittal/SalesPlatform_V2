# Java Action: ImportCSVUsingSQL

> Imports a full CSV dataset using optimized SQL batches. It's very fast and has no impact on memory consumption and able to handle millions of records, but is limited to full loads of simple structures (no associations or inheritance). Attributes will be found based on headers (and spaces will be replaced by _). Non matching attributes won't be imported (warning of skipped headers will be present). Tested target data types: - String - Integer - Long - DateTime (from unix timestamp including ms) - Enumeration - Boolean (accepted: true TRUE false FALSE 0 1) Only tested for PostgreSQL!

**Returns:** `Integer`

## Parameters

| Name | Type | Required |
|---|---|---|
| `file` | ConcreteEntity | ✅ |
| `separator` | String | ✅ |
| `quoteChar` | String | ✅ |
| `skipLines` | Integer | ✅ |
| `targetEntity` | Unknown | ✅ |
| `characterSet` | String | ✅ |
| `decimalSeparator` | String | ✅ |
| `groupingSeparator` | String | ✅ |
