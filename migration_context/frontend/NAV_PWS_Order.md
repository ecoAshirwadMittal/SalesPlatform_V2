# Nanoflow: NAV_PWS_Order

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## ⚙️ Execution Flow

1. **Call Microflow **EcoATM_PWS.DS_BuyerCodeBySession** (Result: **$BuyerCode**)**
2. 🔀 **DECISION:** `$BuyerCode != empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_PWS.ACT_Open_PWS_Order_fromMenu****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **AuctionUI.ACT_CreateBuyerCodeSelectHelper****
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
