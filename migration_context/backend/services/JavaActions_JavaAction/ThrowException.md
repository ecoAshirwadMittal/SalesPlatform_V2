# Java Action: ThrowException

> This action always throws an exception (of type communityutils.UserThrownError), which is, in combination with custom error handling, quite useful to end a microflow prematurely or to bail out to the calling action/ microflow. The message of the last thrown error can be inspected by using the variable $lasterrormessage Example usuage: In general, if an Event (before commit especially) returns false, it should call this action and then return true instead. If an Before commit returns false, the object will not be committed, but there is no easy way for the calling Microflow/ action to detect this! An exception on the other hand, will be noticed.

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `message` | String | ✅ |
