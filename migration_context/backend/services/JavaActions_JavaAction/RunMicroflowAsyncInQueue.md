# Java Action: RunMicroflowAsyncInQueue

> Runs a microflow asynchronous, that is, this action immediately returns but schedules the microflow to be run in the near future. The queue guarantees a first come first serve order of the microflows, and only one action is served at a time. The microflow is run with system rights in its own transaction, and is very useful to run heavy microflows on the background.

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `microflow` | Unknown | ✅ |
