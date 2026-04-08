# Java Action: Clone

> Clones objects - Source: the original object to copy - Target: the object to copy it into (should be of the same type, or a specialization) - includeAssociations: whether to clone associations. If associated objects need to be cloned as well, use deepClone, this function only copies the references, not the reffered objects. Target is not committed automatically.

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `source` | ParameterizedEntity | ✅ |
| `target` | ParameterizedEntity | ✅ |
| `withAssociations` | Boolean | ✅ |
