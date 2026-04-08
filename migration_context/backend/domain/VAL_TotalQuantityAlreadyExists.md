# Microflow Detailed Specification: VAL_TotalQuantityAlreadyExists

### 📥 Inputs (Parameters)
- **$BidDataTotalQuantityConfig_current** (Type: AuctionUI.BidDataTotalQuantityConfig)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$isValid** = `true`**
2. **DB Retrieve **AuctionUI.BidDataTotalQuantityConfig**  (Result: **$BidDataTotalQuantityConfigList**)**
3. **Remove **$$BidDataTotalQuantityConfig_current
** to/from list **$BidDataTotalQuantityConfigList****
4. 🔀 **DECISION:** `$BidDataTotalQuantityConfigList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **FindByExpression** on **$undefined** where `$BidDataTotalQuantityConfig_current/EcoID=$currentObject/EcoID and $BidDataTotalQuantityConfig_current/Grade=$currentObject/Grade` (Result: **$BidDataTotalQuantityConfigExists**)**
      2. 🔀 **DECISION:** `$BidDataTotalQuantityConfigExists=empty`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🏁 **END:** Return `$isValid`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.