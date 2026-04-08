# Microflow Detailed Specification: SUB_Buyer_Save

### 📥 Inputs (Parameters)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_MDM.VAL_Buyer** (Result: **$IsValid**)**
2. 🔀 **DECISION:** `$IsValid`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      2. **Retrieve related **BuyerCode_Buyer** via Association from **$Buyer** (Result: **$BuyerCodeList**)**
      3. **List Operation: **Filter** on **$undefined** where `true` (Result: **$BuyerCodeList_Delete**)**
      4. **List Operation: **Subtract** on **$undefined** (Result: **$BuyerCodeList_toCommit**)**
      5. **Delete**
      6. **Call Microflow **EcoATM_MDM.VAL_BuyerCode** (Result: **$Buyercodes_valid**)**
      7. 🔀 **DECISION:** `$Buyercodes_valid`
         ➔ **If [true]:**
            1. **AggregateList**
            2. **Update **$Buyer**
      - Set **BuyerCodesDisplay** = `$ReducedBuyerCodes`**
            3. **Call Microflow **EcoATM_MDM.SUB_SetBuyerOwnerAndChanger****
            4. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
               │ 1. **Call Microflow **EcoATM_MDM.SUB_SetBuyerCodeOwnerAndChanger****
               └─ **End Loop**
            5. **Commit/Save **$Buyer** to Database**
            6. **Commit/Save **$BuyerCodeList_toCommit** to Database**
            7. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
            8. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBuyerToSnowflake`
               ➔ **If [true]:**
                  1. **CreateList**
                  2. **Add **$$Buyer
** to/from list **$BuyerList****
                  3. **Call Microflow **EcoATM_MDM.SUB_SendBuyerToSnowflake****
                  4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  5. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  2. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.