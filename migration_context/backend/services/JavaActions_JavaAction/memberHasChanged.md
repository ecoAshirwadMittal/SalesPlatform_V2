# Java Action: memberHasChanged

> Checks whether a member has changed since the last commit. Useful in combination with getOriginalValueAsString. - item : the object to inspect - member: the name of the member to inspect. Note that for references, the module name needs to be included. Returns true if changed.

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `item` | ParameterizedEntity | ✅ |
| `member` | String | ✅ |
