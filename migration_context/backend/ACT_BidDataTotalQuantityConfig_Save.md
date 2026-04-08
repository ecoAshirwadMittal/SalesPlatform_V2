# Microflow Detailed Specification: ACT_BidDataTotalQuantityConfig_Save

### 📥 Inputs (Parameters)
- **$BidDataTotalQuantityConfig** (Type: AuctionUI.BidDataTotalQuantityConfig)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.VAL_BidDataTotalQuantityConfig** (Result: **$isValid**)**
2. 🔀 **DECISION:** `$isValid`
   ➔ **If [true]:**
      1. **Commit/Save **$BidDataTotalQuantityConfig** to Database**
      2. **Close current page/popup**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.