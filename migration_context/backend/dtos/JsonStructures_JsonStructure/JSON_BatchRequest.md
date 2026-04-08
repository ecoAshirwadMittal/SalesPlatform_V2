# JSON Structure: JSON_BatchRequest

## Sample JSON

```json
{
    "requests": [
        {
            "id": "1",
            "dependsOn": [
                "1"
            ],
            "method": "GET",
            "url": "/me/drive/root:/{file}:/content",
            "headers": {
                "ConsistencyLevel": "eventual",
                "Content-Type": "application/json"
            },
            "body": {}
        },
        {
            "id": "2",
            "method": "GET",
            "url": "/me/planner/tasks"
        },
        {
            "id": "3",
            "method": "GET",
            "url": "/groups/{id}/events"
        },
        {
            "id": "4",
            "url": "/me",
            "method": "PATCH",
            "body": {
                "city": "Redmond"
            },
            "headers": {
                "Content-Type": "application/json"
            }
        },
        {
            "id": "5",
            "url": "users?$select=id,displayName,userPrincipalName&$filter=city eq null&$count=true",
            "method": "GET",
            "headers": {
                "ConsistencyLevel": "eventual",
                "location": "https://b0mpua-by3301.files.1drv.com/y23vmagahszhxzlcvhasdhasghasodfi"
            }
        }
    ]
}
```
