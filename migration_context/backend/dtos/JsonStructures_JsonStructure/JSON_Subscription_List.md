# JSON Structure: JSON_Subscription_List

## Sample JSON

```json
{
    "@odata.context": "https://graph.microsoft.com/v1.0/$metadata#subscriptions",
    "value": [
        {
            "id": "0fc0d6db-0073-42e5-a186-853da75fb308",
            "resource": "Users",
            "applicationId": "24d3b144-21ae-4080-943f-7067b395b913",
            "changeType": "updated,deleted",
            "clientState": "",
            "notificationUrl": "https://webhookappexample.azurewebsites.net/api/notifications",
            "lifecycleNotificationUrl": "https://webhook.azurewebsites.net/api/send/lifecycleNotifications",
            "expirationDateTime": "2018-03-12T05:00:00.0000000Z",
            "creatorId": "8ee44408-0679-472c-bc2a-692812af3437",
            "latestSupportedTlsVersion": "v1_2",
            "encryptionCertificate": "",
            "encryptionCertificateId": "",
            "includeResourceData": false,
            "notificationContentType": "application/json"
        }
    ]
}
```
