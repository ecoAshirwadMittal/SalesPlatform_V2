# Java Action: ThrowWebserviceException

> (Behavior has changed since version 3.2. The exception is now properly propagated to the cient). Throws an exception. This is very useful if the microflow is called by a webservice. If you throw this kind of exceptions, an fault message will be generated in the output, instead of an '501 Internal server' error. If debug level of community commons is set to 'debug' the errors will be locally visible as well, otherwise not. Throwing a webservice exception states that the webservice invocation was incorrect, not the webservice implementation.

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `faultstring` | String | ✅ |
