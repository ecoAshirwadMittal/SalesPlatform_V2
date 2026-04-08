# Java Action: getOriginalValueAsString

> Returns the original value of an object member, that is, the last committed value. This is useful if you want to compare the actual value with the last stored value. - item : the object of which you want to inspect a member - member: the member to retrieve the previous value from. Note that for references, the module name needs to be included. The function is applicable for non-String members as well, but always returns a String representation of the previous value.

**Returns:** `String`

## Parameters

| Name | Type | Required |
|---|---|---|
| `item` | ParameterizedEntity | ✅ |
| `member` | String | ✅ |
