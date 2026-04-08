# Microflow Detailed Specification: ACT_Buyer_EditSave

### 📥 Inputs (Parameters)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.Val_CheckDisablingBuyer** (Result: **$BuyerCodeValidation**)**
2. 🔀 **DECISION:** `$BuyerCodeValidation = true`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_MDM.SUB_Buyer_Save** (Result: **$Variable**)**
      2. 🔀 **DECISION:** `$Variable`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `false`
         ➔ **If [true]:**
            1. **CreateList**
            2. **Add **$$Buyer
** to/from list **$BuyerList****
            3. **Call Microflow **EcoATM_MDM.SUB_SendBuyerToSnowflake****
            4. **Close current page/popup**
            5. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.