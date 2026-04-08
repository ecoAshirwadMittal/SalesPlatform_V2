# JSON Structure: JSON_Buyer

## Sample JSON

```json
[
  {
    "SubmissionID": 1,
    "CompanyName": "Tech Solutions Inc.",
    "Status": "Active",
    "BuyerCodesDisplay": "TS01, TS02",
    "IsFailedBuyerDisable": false,
    "CreatedBy": "user1",
    "ChangedBy": "admin",
    "CreatedOn": "2024-11-18T10:00:00Z",
    "ChangedOn": "2024-11-18T12:00:00Z",
    "isSpecialBuyer": true,
    "salesRep": {
      "firstName": "alva",
      "lastName": "Nulpart",
      "salesRepId": 1
    },
    "Codes": [
      {
        "BuyerCodeType": "Primary",
        "Code": "TS01",
        "Status": "Active",
        "Budget": 5000,
        "CreatedBy": "user1",
        "ChangedBy": "admin",
        "CreatedOn": "2024-11-18T10:00:00Z",
        "ChangedOn": "2024-11-18T12:00:00Z"
      },
      {
        "BuyerCodeType": "Secondary",
        "Code": "TS03",
        "Status": "Active",
        "Budget": 3000,
        "CreatedBy": "user1",
        "ChangedBy": "admin",
        "CreatedOn": "2024-11-18T10:15:00Z",
        "ChangedOn": "2024-11-18T12:15:00Z"
      }
    ]
  },
  {
    "SubmissionID": 2,
    "CompanyName": "Innovative Buyers LLC",
    "Status": "Inactive",
    "BuyerCodesDisplay": "IB01",
    "IsFailedBuyerDisable": true,
    "CreatedBy": "user2",
    "ChangedBy": "admin",
    "CreatedOn": "2024-11-18T11:00:00Z",
    "ChangedOn": "2024-11-18T13:00:00Z",
    "isSpecialBuyer": true,
    "Codes": [
      {
        "BuyerCodeType": "Primary",
        "Code": "IB01",
        "Status": "Inactive",
        "Budget": 2000,
        "CreatedBy": "user2",
        "ChangedBy": "admin",
        "CreatedOn": "2024-11-18T11:00:00Z",
        "ChangedOn": "2024-11-18T13:00:00Z"
      }
    ]
  },
  {
    "SubmissionID": 3,
    "CompanyName": "New Ventures Ltd.",
    "Status": "Active",
    "BuyerCodesDisplay": "NV01, NV02",
    "IsFailedBuyerDisable": false,
    "CreatedBy": "user3",
    "ChangedBy": "admin",
    "CreatedOn": "2024-11-18T11:30:00Z",
    "ChangedOn": "2024-11-18T14:00:00Z",
    "isSpecialBuyer": true,
    "Codes": [
      {
        "BuyerCodeType": "Primary",
        "Code": "NV01",
        "Status": "Active",
        "Budget": 1000,
        "CreatedBy": "user3",
        "ChangedBy": "admin",
        "CreatedOn": "2024-11-18T11:30:00Z",
        "ChangedOn": "2024-11-18T14:00:00Z"
      },
      {
        "BuyerCodeType": "Primary",
        "Code": "NV01",
        "Status": "Active",
        "Budget": 1000,
        "CreatedBy": "user3",
        "ChangedBy": "admin",
        "CreatedOn": "2024-11-18T11:30:00Z",
        "ChangedOn": "2024-11-18T14:00:00Z"
      }
    ]
  }
]
```
