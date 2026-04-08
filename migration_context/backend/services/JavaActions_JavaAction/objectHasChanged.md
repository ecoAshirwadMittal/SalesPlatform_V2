# Java Action: objectHasChanged

> Returns true if at least one member (including owned associations) of this object has changed. For Mendix < 9.5 this action keeps track of changes to the object except when 'changing to the same value'. For Mendix >= 9.5 this action keeps track of all changes. This is a result of a change of the underlying Mendix runtime-server behaviour.

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `item` | ParameterizedEntity | ✅ |
