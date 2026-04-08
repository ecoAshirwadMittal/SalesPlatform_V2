# Java Action: executeUnverifiedMicroflowInBatches

> Invokes a microflow in batches. The microflow is invoked for each individual item returned by the xpath query. The objects will be processed in small batches (based on the batchsize), which makes this function very useful to process large amounts of objects without using much memory. All defaut behavior such as commit events are applied as defined in your microflow. Parameters: - xpath: Fully qualified xpath query that indicates the set of objects the microflow should be invoked on. For example: '//System.User[Active = true()]' - microflow: The microflow that should be invoked. Should accept one argument of the same type as the xpath. For example: 'MyFirstModule.UpdateBirthday' - batchsize: The amount of objects that should be processed in a single transaction. When in doubt, 1 is fine, but larger batches (for example; 100) will be faster due to less overhead. - waitUntilFinished: Whether this call should block (wait) until all objects are processed. Returns true if the batch has successfully started, or, if waitUntilFinished is true, returns true if the batch succeeded completely. Note, if new objects are added to the dataset while the batch is still running, those objects will be processed as well.

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `xpath` | String | ✅ |
| `microflowName` | String | ✅ |
| `batchsize` | Integer | ✅ |
| `waitUntilFinished` | Boolean | ✅ |
| `ascending` | Boolean | ✅ |
