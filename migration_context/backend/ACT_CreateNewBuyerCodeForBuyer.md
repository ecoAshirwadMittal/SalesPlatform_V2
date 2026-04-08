# Microflow Detailed Specification: ACT_CreateNewBuyerCodeForBuyer

### 📥 Inputs (Parameters)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Commit/Save **$Buyer** to Database**
2. **Create **EcoATM_BuyerManagement.BuyerCode** (Result: **$NewBuyerCode**)
      - Set **BuyerCode_Buyer** = `$Buyer`
      - Set **Status** = `AuctionUI.enum_BuyerCodeStatus.Active`**
3. **Maps to Page: **AuctionUI.BuyerCode_Edit****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.