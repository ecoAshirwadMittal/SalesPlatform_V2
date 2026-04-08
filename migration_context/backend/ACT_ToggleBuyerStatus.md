# Microflow Detailed Specification: ACT_ToggleBuyerStatus

### 📥 Inputs (Parameters)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Buyer/Status = AuctionUI.enum_BuyerStatus.Active`
   ➔ **If [true]:**
      1. **Update **$Buyer**
      - Set **Status** = `AuctionUI.enum_BuyerStatus.Disabled`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$Buyer**
      - Set **Status** = `AuctionUI.enum_BuyerStatus.Active`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.