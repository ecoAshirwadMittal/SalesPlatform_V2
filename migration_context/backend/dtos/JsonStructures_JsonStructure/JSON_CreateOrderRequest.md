# JSON Structure: JSON_CreateOrderRequest

## Sample JSON

```json
{
  "request": {
    "originSystemOrderId": "100001",
    "orderType": "PWS",
    "orderDate": "20240121201900",
    "buyerCode": "BC1",
    "originSystemUser": "john.doe",
    "shippingInstructions": "Eco Buyer Guide",
    "freeFormOrd01": "Freeform Text 1",
    "freeFormOrd02": "Freeform Text 2",
    "freeFormOrd03": "Freeform Text 3",
    "orderLine": [
      {
        "item_number": "PWS296068411893",
        "Quantity": "IPHONE 11 PRO",
        "unitSellingPrice": "T-MOBILE",
        "originSystemLineId": "0064GB",
        "freeFormLine01": "Freeform Text 1",
        "freeFormLine02": "Freeform Text 2",
        "freeFormLine03": "Freeform Text 3"
      }
    ]
  }
}
```
