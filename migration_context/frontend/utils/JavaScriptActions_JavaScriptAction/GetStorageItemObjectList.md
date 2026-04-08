# JavaScript Action: GetStorageItemObjectList

> Retrieve a local stored list of Mendix objects identified by a unique key. When objects are the client state it will be returned, if not they will be re-created. Note: when re-creating the local Mendix object the Mendix Object ID will never be the same.

**Returns:** `List`

## Parameters

| Name | Type | Required |
|---|---|---|
| `Key` | String | ✅ |
| `Entity` | Unknown | ✅ |
