# Microflow Detailed Specification: ACT_Open_PWS_Order_fromMenu

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'OpenPWSOrder'`**
2. **Create Variable **$Description** = `'Open PWS Order'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
5. 🔀 **DECISION:** `$FeatureFlagState`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. **Show Message (Information): `You do not have access to this page, ask your administrator for beta access.`**
      3. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BuyerCode != empty`
         ➔ **If [true]:**
            1. **Retrieve related **PWSSearch_Session** via Association from **$currentSession** (Result: **$PWSSearchList**)**
            2. **Delete**
            3. **Retrieve related **BuyerCode_Session** via Association from **$currentSession** (Result: **$BuyerCodeList**)**
            4. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
               │ 1. **Update **$IteratorBuyerCode**
      - Set **BuyerCode_Session** = `empty`**
               └─ **End Loop**
            5. **Commit/Save **$BuyerCodeList** to Database**
            6. **Update **$BuyerCode** (and Save to DB)
      - Set **BuyerCode_Session** = `$currentSession`**
            7. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
            8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            9. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. **Call Microflow **AuctionUI.ACT_CreateBuyerCodeSelectHelper****
            3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.