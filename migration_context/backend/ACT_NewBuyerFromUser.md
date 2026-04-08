# Microflow Detailed Specification: ACT_NewBuyerFromUser

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_BuyerManagement.NewBuyerHelper** (Result: **$NewBuyerHelper**)
      - Set **NewBuyerHelper_EcoATMDirectUser** = `$EcoATMDirectUser`
      - Set **SourcePage** = `'user'`**
2. **Create **EcoATM_BuyerManagement.Buyer** (Result: **$NewBuyer**)
      - Set **Status** = `AuctionUI.enum_BuyerStatus.Active`**
3. **Maps to Page: **EcoATM_MDM.Buyer_New****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.