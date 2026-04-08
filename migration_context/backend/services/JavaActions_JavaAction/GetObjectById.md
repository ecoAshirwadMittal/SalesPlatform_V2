# Java Action: GetObjectById

> Gets EntityType object from given ObjectId (Guid). Scheduler batch processes cannot have parameters. Via QueuedAction-QueuedActionParameters it became possible to pass generic string parameters (param1..param3) to a batch process. Now it is possible to pass the Guid of an ObjectId as generic string param and on processing the batch fetch the actual object via GetObjectId in a generic way. Like this you can pass objects to a Scheduler batch process. Like this you can avoid putting microflows directlly on queue (fixed in Studio) but always use the Scheduler so you can track your batches on Scheduler Running tab.

**Returns:** `ParameterizedEntity`

## Parameters

| Name | Type | Required |
|---|---|---|
| `ObjectId` | Integer | ✅ |
| `EntityType` | Unknown | ✅ |
