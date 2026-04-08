# JSON Structure: JSON_ChangeNotification_LifecycleEvent

## Sample JSON

```json
{
    "validationTokens": [
        "String"
    ],
    "value": [
        {
            "subscriptionId": "<subscription_guid>",
            "subscriptionExpirationDateTime": "2019-03-20T11:00:00.0000000Z",
            "tenantId": "<tenant_guid>",
            "clientState": "<secretClientState>",
            "lifecycleEvent": "subscriptionRemoved"
        }
    ]
}
```
