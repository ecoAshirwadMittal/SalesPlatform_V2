# JSON Structure: JSON_GetListItemsResponse

## Sample JSON

```json
{
    "value": [{
            "createdDateTime": "2021-12-07T11:59:27.0000000Z",
            "eTag": "\"XXXXXXX\"",
            "id": "1",
            "lastModifiedDateTime": "2021-12-10T09:50:09.0000000Z",
            "webUrl": "https://aXXXXXX.pptx",
            "createdBy": {
                "user": {
                    "email": "XXXXXX",
                    "id": "12341341324abc",
                    "displayName": "Admins"
                }
            },
            "lastModifiedBy": {
                "user": {
                    "email": "test@bla.nl",
                    "id": "123445454545324abc",
                    "displayName": "test"
                }
            },
            "parentReference": {
                "id": "4564564564569",
                "siteId": "test.sharepoint.com,234234234234,678678678678678"
            },
            "contentType": {
                "id": "0x495674576495764958",
                "name": "Document"
            },
        "driveItem": {
            "@microsoft.graph.downloadUrl": "https://XXXXXXd.sharepoint.com/sites/Mendix/_layouts/15/download.aspxXXXX&ApiVersion=2.0",
            "createdDateTime": "2021-12-07T11:59:27Z",
			"description":  "XXXXXX",
            "eTag": "\"{1BA5D211-056B-4844-A444-007D6C58B4D6},3\"",
            "id": "01XGWWDEAR2KSRW2YFIREKIRAAPVWFRNGW",
            "lastModifiedDateTime": "2021-12-10T09:50:09Z",
            "name": "XXXXXX.pptx",
            "webUrl": "https://XXXXX.sharepoint.com/sites/Mendix/_layouts/15XXXXX&action=edit&mobileredirect=true",
            "cTag": "\"c:{XXXX},6\"",
            "size": 2569049,
            "createdBy": {
                "user": {
                    "email": "XXXX@XXXX.onmicrosoft.com",
                    "id": "XXXXX",
                    "displayName": "Admin"
                }
            },
            "lastModifiedBy": {
                "user": {
                    "email": "XXXX@XXXX.nl",
                    "id": "XXXXX",
                    "displayName": "XXXX"
                }
            },
            "file": {
                "mimeType": "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "hashes": {
                    "quickXorHash": "wropBwwaDKlWPAGZRknuxYqvwhU="
                }
            },
            "fileSystemInfo": {
                "createdDateTime": "2021-12-07T11:59:27Z",
                "lastModifiedDateTime": "2021-12-10T09:50:09Z"
            }
		}
	}]
}
```
