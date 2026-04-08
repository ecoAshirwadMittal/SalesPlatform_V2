# Java Action: JA_SnowflakeToMendix

> This function allows, from a list of objects (AnyMendixObjectList parameter) to create another list of other objects (of type TargetObject parameter). The target object is automatically created and populated (based on the example object passed as a parameter or declared into the mapping) in json format (JSONMapping parameter). The created objects are committed automatically at the end of the process. Mapping example: { "mapping": { "ECOATM_CODE": "ProductId", "MERGEDGRADE": "Grade" } }

**Returns:** `Void`

## Parameters

| Name | Type | Required |
|---|---|---|
| `AnyMendixObjectList` | List | ✅ |
| `JSONMapping` | String | ✅ |
| `TargetMendixObject` | ParameterizedEntity | ❌ |
