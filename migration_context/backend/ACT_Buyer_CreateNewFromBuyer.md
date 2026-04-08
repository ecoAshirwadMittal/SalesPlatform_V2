# Microflow Detailed Specification: ACT_Buyer_CreateNewFromBuyer

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_BuyerManagement.Buyer** (Result: **$NewBuyer**)
      - Set **Status** = `AuctionUI.enum_BuyerStatus.Active`**
2. **Create **EcoATM_BuyerManagement.NewBuyerHelper** (Result: **$NewBuyerHelper**)
      - Set **SourcePage** = `'buyer'`**
3. **Maps to Page: **EcoATM_MDM.Buyer_New****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.