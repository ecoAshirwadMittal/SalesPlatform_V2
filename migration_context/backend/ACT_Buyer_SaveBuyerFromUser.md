# Microflow Detailed Specification: ACT_Buyer_SaveBuyerFromUser

### 📥 Inputs (Parameters)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)
- **$NewBuyerHelper** (Type: EcoATM_BuyerManagement.NewBuyerHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_MDM.SUB_Buyer_Save** (Result: **$Valid**)**
2. 🔀 **DECISION:** `$Valid`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$NewBuyerHelper!=empty`
         ➔ **If [true]:**
            1. **Retrieve related **NewBuyerHelper_EcoATMDirectUser** via Association from **$NewBuyerHelper** (Result: **$EcoATMDirectUser**)**
            2. 🔀 **DECISION:** `$EcoATMDirectUser = empty`
               ➔ **If [true]:**
                  1. **Close current page/popup**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$EcoATMDirectUser**
      - Set **EcoATMDirectUser_Buyer** = `$Buyer`**
                  2. **Close current page/popup**
                  3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Close current page/popup**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.