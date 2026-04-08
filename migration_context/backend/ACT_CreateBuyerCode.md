# Microflow Detailed Specification: ACT_CreateBuyerCode

### 📥 Inputs (Parameters)
- **$BuyerCode_Helper** (Type: EcoATM_BuyerManagement.BuyerCode_Helper)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$BuyerCode_Helper**
      - Set **Code** = `toUpperCase($BuyerCode_Helper/Code)`**
2. **Call Microflow **AuctionUI.VAL_ValidateBuyerCode** (Result: **$isValid**)**
3. 🔀 **DECISION:** `$isValid`
   ➔ **If [true]:**
      1. **Update **$Buyer** (and Save to DB)
      - Set **Status** = `AuctionUI.enum_BuyerStatus.Active`**
      2. **Commit/Save **$Buyer** to Database**
      3. **Create **EcoATM_BuyerManagement.BuyerCode** (Result: **$NewBuyerCode**)
      - Set **Code** = `$BuyerCode_Helper/Code`
      - Set **Status** = `AuctionUI.enum_BuyerCodeStatus.Active`
      - Set **BuyerCodeType** = `$BuyerCode_Helper/BuyerCodeType`
      - Set **BuyerCode_Buyer** = `$Buyer`**
      4. **Retrieve related **BuyerCode_Buyer** via Association from **$Buyer** (Result: **$BuyerCodeList**)**
      5. **Add **$$NewBuyerCode** to/from list **$BuyerCodeList****
      6. **Commit/Save **$Buyer** to Database**
      7. **Update **$BuyerCode_Helper**
      - Set **Code** = `empty`**
      8. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.