# Microflow Detailed Specification: ACT_BuyerCode_Create

### 📥 Inputs (Parameters)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_BuyerManagement.BuyerCode** (Result: **$NewBuyerCode**)
      - Set **BuyerCode_Buyer** = `$Buyer`
      - Set **Status** = `AuctionUI.enum_BuyerCodeStatus.Active`**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.