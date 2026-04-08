# Java Action: refreshClass

> Refreshes a certain domain object type in the client. Useful to enforce a datagrid to refresh for example. -objectType : Type of the domain objects to refresh, such as System.User or MyModule.MyFirstEntity. (you can use getTypeAsString to determine this dynamically, so that the invoke of this action is not be sensitive to domain model changes).

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `objectType` | String | ✅ |
