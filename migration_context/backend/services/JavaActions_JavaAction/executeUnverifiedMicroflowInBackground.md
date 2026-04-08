# Java Action: executeUnverifiedMicroflowInBackground

> This action allows an microflow to be executed independently from this microflow. This function is identical to "RunMicroflowAsyncInQueue", except that it takes one argument which will be passed to the microflow being called. This might be useful to model for example your own batching system, or to run a microflow in its own (system) transaction. The microflow is delayed for at least 200ms and then run with low priority in a system context. Since the microflow run in its own transaction, it is not affected with rollbacks (due to exceptions) or commits in this microflow. Invocations to this method are guaranteed to be run in FIFO order, only one microflow is run at a time. Note that since the microflow is run as system transaction, $currentUser is not available and no security restrictions are applied. - The microflowname specifies the fully qualified name of the microflow (case sensitive) e.g.: 'MyFirstModule.MyFirstMicroflow'. - The context object specifies an argument that should be passed to the microflow if applicable. Currently only zero or one argument are supported. Note that editing this object in both microflows might lead to unexpected behavior. Returns true if scheduled successfully.

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `microflowName` | String | ✅ |
| `contextObject` | ParameterizedEntity | ✅ |
