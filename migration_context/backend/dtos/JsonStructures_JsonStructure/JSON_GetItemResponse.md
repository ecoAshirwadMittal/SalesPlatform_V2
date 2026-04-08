# JSON Structure: JSON_GetItemResponse

## Sample JSON

```json
{
    "@odata.context": "https://graph.microsoft.com/v1.0/$metadata#sites('XXXXX')/lists('XXXXX')/items(fields())/$entity",
    "@odata.etag": "\"XXXXXX\"",
    "createdDateTime": "2021-12-07T11:59:27Z",
    "eTag": "\"XXXXXXX,2\"",
    "id": "1",
    "lastModifiedDateTime": "2021-12-10T09:50:09Z",
    "webUrl": "https://XXXXXX.sharepoint.com/sites/MendixPOC/Shared%20Documents/XXXXXX.pptx",
    "createdBy": {
        "user": {
            "email": "XXXXX@.onmicrosoft.com",
            "id": "XXXXXXX",
            "displayName": "Admin"
        }
    },
    "lastModifiedBy": {
        "user": {
            "email": "XXXXXX@XXXX.nl",
            "id": "XXXXXXXX",
            "displayName": "XXXXXX"
        }
    },
    "parentReference": {
        "id": "XXXXXX",
        "siteId": "XXXXX.sharepoint.com,XXXXXXX,XXXXXX"
    },
    "contentType": {
        "id": "0x0101007ABBA57FEC38674B9B43DC75AC6CEB7F",
        "name": "Document"
    },
    "fields@odata.context": "https://graph.microsoft.com/v1.0/$metadata#sites('XXXXXX')/lists('XXXXX')/items('1')/fields/$entity",
    "fields": {
        "@odata.etag": "\"XXXXXXXX,2\"",
        "FileLeafRef": "XXXXXXX.pptx",
        "Title": "XXXXXXXX",
        "DocumentType": "Test Document",
        "id": "1",
        "ContentType": "Document",
        "Created": "2021-12-07T11:59:27Z",
        "AuthorLookupId": "6",
        "Modified": "2021-12-10T09:50:09Z",
        "EditorLookupId": "13",
        "_CheckinComment": "",
        "LinkFilenameNoMenu": "XXXXXX.pptx",
        "LinkFilename": "XXXXXXX.pptx",
        "DocIcon": "pptx",
        "FileSizeDisplay": "2569049",
        "ItemChildCount": "0",
        "FolderChildCount": "0",
        "_ComplianceFlags": "",
        "_ComplianceTag": "",
        "_ComplianceTagWrittenTime": "",
        "_ComplianceTagUserId": "",
        "_CommentCount": "",
        "_LikeCount": "",
        "_DisplayName": "",
        "Edit": "0",
        "_UIVersionString": "2.0",
        "ParentVersionStringLookupId": "1",
        "ParentLeafNameLookupId": "1"
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
}
```
