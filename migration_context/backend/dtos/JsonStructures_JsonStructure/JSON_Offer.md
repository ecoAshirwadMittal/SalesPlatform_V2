# JSON Structure: JSON_Offer

## Sample JSON

```json
{
  "offer": {
    "offerId": "WP-000-000",
    "offerSKUCount": 100,
    "offerTotalQuantity": 10,
    "offerTotalPrice": 1000,
    "offerSubmissionDate": "2025-05-22T20:00:00Z",
    "offerAvgPrice": 90.5,
    "offerMinPercentageVariance": 10.5,
    "offerRevertedDate": "2025-05-22T20:00:00Z",
    "updateDate": "2025-05-22T00:00:00Z",
    "counterOfferTotalSKU": 10,
    "counterOfferTotalQty": 2,
    "counterOfferTotalPrice": 1100,
    "counterOfferAvgPrice": 10.89,
    "counterOfferMinPercentage": 1.11,
    "counterResponseSubmittedOn": "2025-05-19T10:00:00Z",
    "finalOfferTotalSKU": 10,
    "finalOfferTotalQty": 10,
    "finalOfferTotalPrice": 1000,
    "finalOfferSubmittedOn": "2025-05-19T10:00:00Z",
    "isValidOffer": true,
    "salesReviewCompletedOn": "2025-05-19T10:00:00Z",
    "offerBeyondSLA": false,
    "sameSKUOffer": false,
    "buyerOfferCancelled": true,
    "sellerOfferCancelled": false,
    "offerCancelledOn": "2025-05-19T10:00:00Z",
    "offerType": "ENUM",
    "offerItems": [
      {
        "offerQuantity": 100,
        "offerPrice": 10,
        "offerTotalPrice": 1000,
        "salesOfferItemStatus": "Finalize",
        "counterPrice": 15,
        "counterQuantity": 2,
        "counterTotal": 30,
        "finalOfferPrice": 16,
        "finalOfferQuantity": 1,
        "finalOfferTotalPrice": 16,
        "ShippedPrice": 16,
        "ShippedQty": 16,
        "buyerCounterStatus": "Accept",
        "minPercentage": 10.4,
        "listPercentage": 10.11,
        "sameSKUOfferAvailable": false,
        "offerDrawerStatus": "Ordered",
        "reserved": false,
        "reservedOn": "2025-05-19T10:00:00Z",
        "orderSynced": true,
        "device": {
          "sku": "wwwwwwwwwww-wwww",
          "ATPQuantity":1,
          "AvlQuantity":1,
          "ReservedQuantity":1
        }
  
      }
    ],
    "submittedBy": {
      "email": "PWS-CODE"
    },
    "OfferStatus": {
      "SystemStatus": "Status"
    },
    "counteredBy": {
      "email": "PWS-CODE"
    },
    "salesRep": {
      "email": "PWS-CODE"
    },
    "revertedBy": {
      "email": "PWS-CODE"
    },
    "cancelledBy": {
      "email": "PWS-CODE"
    },
    "salesRepresentative": {
      "name": "PWS-CODE"
    },
    "buyerCode": {
      "code": "PWS-CODE"
    },
    "order": {
      "orderNumber": "www",
      "orderDate": "2025-05-19T10:00:00Z",
      "hasShipmentDetails": true,
      "shipDate": "2025-05-19T10:00:00Z",
      "shipMethod": "www",
      "shipToAddress": "www",
      "ShippedTotalSKU": 2,
      "ShippedTotalQuantity": 2,
      "ShippedTotalPrice": 2
    }
  }
}
```
