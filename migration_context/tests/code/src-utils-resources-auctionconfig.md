# Utility: auction_config.json

- **Path**: `src\utils\resources\auction_config.json`
- **Category**: Utility
- **Lines**: 47
- **Size**: 1,201 bytes

## Source Code

```json
{
    "minimum_bid_price": "2.00",
    "additional_qty_config": [
        {
            "productID": "12238",
            "grade": "A_YYY",
            "data_wipe_qty": "10",
            "additional_qty": "5"
        },
        {
            "productID": "12238",
            "grade": "B_NYY/D_NNY",
            "data_wipe_qty": "10",
            "additional_qty": "5"
        },
        {
            "productID": "12238",
            "grade": "C_YNY/G_YNN",
            "data_wipe_qty": "0",
            "additional_qty": "5"
        },
        {
            "productID": "12238",
            "grade": "E_YYN",
            "data_wipe_qty": "0",
            "additional_qty": "5"
        }
    ],
    "round_2_criteria": {
        "TGP_Percent": "0.15",
        "TGP_Value": "15",
        "Buyer_Qulification": [
            "All Buyers",
            "Any Buyer with Round-1 Bids",
            "Buyer with Target Qualification"
        ],
        "Inventory_Qulification": [
            "Full Inventory",
            "Inventory with Round-1 Bids",
            "Inventory Based on Target Qualification"
        ],
        "Special_Setting_Flag": [
            "Yes",
            "No"
        ]
    }
}
```
