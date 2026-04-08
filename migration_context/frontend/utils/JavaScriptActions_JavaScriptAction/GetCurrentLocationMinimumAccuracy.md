# JavaScript Action: GetCurrentLocationMinimumAccuracy

> This action retrieves the current geographical position of a user/device with a minimum accuracy as parameter. If a position is not acquired with minimum accuracy within a specific timeout it will retrieve the last most precise location. Since this can compromise privacy, the position is not available unless the user approves it. The web browser will request the permission at the first time the location is requested. When denied by the user it will not prompt a second time. On hybrid and native platforms the permission can be requested with the `RequestLocationPermission` action. Best practices: https://developers.google.com/web/fundamentals/native-hardware/user-location/

**Returns:** `ConcreteEntity`

## Parameters

| Name | Type | Required |
|---|---|---|
| `Timeout` | Integer | ✅ |
| `MaximumAge` | Integer | ✅ |
| `HighAccuracy` | Boolean | ✅ |
| `minimumAccuracy` | Integer | ✅ |
