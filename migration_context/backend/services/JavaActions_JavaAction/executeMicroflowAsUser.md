# Java Action: executeMicroflowAsUser

> Executes the given microflow as if the $currentuser is the provided user (delegation). Use sudoContext to determine if 'apply entity access' should be used - microflowName: the fully qualified microflow name, 'CommunityCommons.CreateUserIfNotExists' - username: The user that should be used to execute the microflow - sudoContext: whether entity access should be applied.

**Returns:** `String`

## Parameters

| Name | Type | Required |
|---|---|---|
| `microflow` | Unknown | ✅ |
| `username` | String | ✅ |
| `sudoContext` | Boolean | ✅ |
