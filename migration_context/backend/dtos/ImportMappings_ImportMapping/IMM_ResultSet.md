# Import Mapping: IMM_ResultSet

**JSON Structure:** `SnowflakeRESTSQL.JSON_ResultSet`

## Mapping Structure

- **Root** (Object) → `SnowflakeRESTSQL.ResultSet`
  - **Code** (Value)
    - Attribute: `SnowflakeRESTSQL.ResultSet.Code`
  - **StatementHandle** (Value)
    - Attribute: `SnowflakeRESTSQL.ResultSet.StatementHandle`
  - **Message** (Value)
    - Attribute: `SnowflakeRESTSQL.ResultSet.Message`
  - **NumRows** (Value)
    - Attribute: `SnowflakeRESTSQL.ResultSet.NumRows`
  - **JsonObject** (Object) → `SnowflakeRESTSQL.PartitionInfo`
    - **RowCount** (Value)
      - Attribute: `SnowflakeRESTSQL.PartitionInfo.RowCount`
