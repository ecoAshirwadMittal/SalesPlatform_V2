# JSON Structure: JSON_BatchResponse

## Sample JSON

```json
{
    "responses": [
        {
            "id": "1",
            "status": 302,
            "headers": {
                "location": "https://b0mpua-by3301.files.1drv.com/y23vmagahszhxzlcvhasdhasghasodfi"
            }
        },
        {
            "id": "3",
            "status": 401,
            "body": {
                "error": {
                    "code": "Forbidden",
                    "message": "..."
                }
            }
        },
        {
            "id": "5",
            "status": 200,
            "headers": {
                "OData-Version": "4.0"
            },
            "body": {
                "@odata.context": "https://graph.microsoft.com/v1.0/$metadata#users(id,displayName,userPrincipalName)",
                "@odata.count": 12,
                "value": [
                    {
                        "id": "071cc716-8147-4397-a5ba-b2105951cc0b",
                        "displayName": "Adele Vance",
                        "userPrincipalName": "AdeleV@Contoso.com"
                    }
                ]
            }
        },
        {
            "id": "2",
            "status": 200,
            "body": {
                "@odata.context": "https://graph.microsoft.com/v1.0/$metadata#Collection(microsoft.graph.plannerTask)",
                "value": []
            }
        },
        {
            "id": "4",
            "status": 204,
            "body": null
        }
    ]
}
```
