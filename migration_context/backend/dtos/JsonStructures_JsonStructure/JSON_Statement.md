# JSON Structure: JSON_Statement

## Sample JSON

```json
{
    "statement": "select * from T where c1=?",
    "timeout": 60,
    "database": "TESTDB",
    "schema": "TESTSCHEMA",
    "warehouse": "TESTWH",
    "role": "TESTROLE",
    "bindings": {
        "1": {
            "type": "FIXED",
            "value": "123"
        }
    }
}
```
