# Microflow Detailed Specification: SUB_BuyerOffer_GetOrCreate

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$BuyerCode!=empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWS.BuyerOffer** Filter: `[EcoATM_PWS.BuyerOffer_BuyerCode=$BuyerCode]` (Result: **$BuyerOffer**)**
      2. 🔀 **DECISION:** `$BuyerCode!=empty`
         ➔ **If [true]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. 🏁 **END:** Return `$BuyerOffer`
         ➔ **If [false]:**
            1. **Create **EcoATM_PWS.BuyerOffer** (Result: **$NewBuyerOffer**)
      - Set **BuyerOffer_BuyerCode** = `$BuyerCode`**
            2. **Call Microflow **Custom_Logging.SUB_Log_Info****
            3. 🏁 **END:** Return `$NewBuyerOffer`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.