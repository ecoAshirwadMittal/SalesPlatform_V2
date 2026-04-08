# Java Action: ExecuteMicroflow

> Executes specified Microflow on specified TaskQueue with specified QueuedAction parameter (optional) as specified RunAsUser userName (optional)

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `microflowName` | String | ✅ |
| `taskQueue` | String | ✅ |
| `arg1Name` | String | ❌ |
| `arg1Value` | ParameterizedEntity | ❌ |
| `userName` | String | ❌ |
