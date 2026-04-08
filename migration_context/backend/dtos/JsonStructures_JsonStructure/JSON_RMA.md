# JSON Structure: JSON_RMA

## Sample JSON

```json

  {
    "Number": "RMA-001",
    "Status": "Open",
    "RequestSKUs": 10,
    "RequestQty": 10,
    "RequestSalesTotal": 1000,
    "ApprovalDate": "2025-08-01T10:00:00",
    "ApprovedSKUs": 8,
    "ApprovedQty": 8,
    "ApprovedSalesTotal": 800,
    "SubmittedDate": "2025-07-30T15:00:00",
    "ApprovedCount": 8,
    "DeclinedCount": 2,
    "createdBy": "user.alice",
    "createdOn": "2025-07-25T09:00:00",
    "changedBy": "user.bob",
    "changedOn": "2025-08-01T12:00:00",
    "RMAItems": [
      {
        "IMEI": "123456789012345",
        "ShipDate": "2025-07-25T13:00:00",
        "ReturnReason": "Defective",
        "Status": "Approved",
        "OrderNumber": "ORD-001",
        "SalePrice": 100,
        "createdBy": "user.alice",
        "createdOn": "2025-07-25T14:00:00",
        "changedBy": "user.bob",
        "changedOn": "2025-08-01T12:30:00"
      },
      {
        "IMEI": "987654321098765",
        "ShipDate": "2025-07-25T13:00:00",
        "ReturnReason": "Wrong item",
        "Status": "Declined",
        "OrderNumber": "ORD-002",
        "SalePrice": 100,
        "createdBy": "user.alice",
        "createdOn": "2025-07-25T14:05:00",
        "changedBy": "user.charlie",
        "changedOn": "2025-08-01T13:00:00"
      }
    ]
  }


```
