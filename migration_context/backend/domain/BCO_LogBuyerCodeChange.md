# Microflow Detailed Specification: BCO_LogBuyerCodeChange

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'BeforeCommitLogBuyerCodeChange'`**
2. **Create Variable **$Description** = `'Before Commit Log Buyer Code Change, BuyerCode: ' + $BuyerCode/Code`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[Code = $BuyerCode/Code]` (Result: **$OriginalBuyerCode**)**
5. 🔀 **DECISION:** `$OriginalBuyerCode != empty`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🔀 **DECISION:** `$OriginalBuyerCode/BuyerCodeType != $BuyerCode/BuyerCodeType`
         ➔ **If [true]:**
            1. **Create **EcoATM_BuyerManagement.BuyerCodeChangeLog** (Result: **$BuyerCodeTypeChangeLog**)
      - Set **ActionType** = `EcoATM_BuyerManagement.enum_BuyerCodeChangeType.Update`
      - Set **OldBuyerCodeType** = `toString($OriginalBuyerCode/BuyerCodeType)`
      - Set **NewBuyerCodeType** = `toString($BuyerCode/BuyerCodeType)`
      - Set **BuyerCodeChangeLog_BuyerCode** = `$BuyerCode`**
            2. 🔀 **DECISION:** `$OriginalBuyerCode/softDelete != $BuyerCode/softDelete`
               ➔ **If [true]:**
                  1. **Create **EcoATM_BuyerManagement.BuyerCodeChangeLog** (Result: **$BuyerCodeSoftDeleteLog**)
      - Set **ActionType** = `EcoATM_BuyerManagement.enum_BuyerCodeChangeType.Update`
      - Set **OldBuyerCodeType** = `toString($OriginalBuyerCode/BuyerCodeType)`
      - Set **NewBuyerCodeType** = `toString($BuyerCode/BuyerCodeType)`
      - Set **BuyerCodeChangeLog_BuyerCode** = `$BuyerCode`**
                  2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  3. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  2. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$OriginalBuyerCode/softDelete != $BuyerCode/softDelete`
               ➔ **If [true]:**
                  1. **Create **EcoATM_BuyerManagement.BuyerCodeChangeLog** (Result: **$BuyerCodeSoftDeleteLog**)
      - Set **ActionType** = `EcoATM_BuyerManagement.enum_BuyerCodeChangeType.Update`
      - Set **OldBuyerCodeType** = `toString($OriginalBuyerCode/BuyerCodeType)`
      - Set **NewBuyerCodeType** = `toString($BuyerCode/BuyerCodeType)`
      - Set **BuyerCodeChangeLog_BuyerCode** = `$BuyerCode`**
                  2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  3. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  2. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.