# Java Action: CountRowsOQLStatement

> This action executes the OQL statement and returns the amount of rows which will be returned by the OQL statement. The main purpose of this action was to avoid overhead of object creation while the interest is to determine if a record within the database exists.

**Returns:** `Integer`

## Parameters

| Name | Type | Required |
|---|---|---|
| `statement` | String | ✅ |
| `amount` | Integer | ✅ |
| `offset` | Integer | ✅ |
