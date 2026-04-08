# Microflow Detailed Specification: SUB_GenerateRound3_BidDataFiles

### 📥 Inputs (Parameters)
- **$BuyerCodeList** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
2. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/GenerateRound3Files`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      2. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
         │ 1. **Call Microflow **EcoATM_BuyerManagement.ACT_BidDataDoc_PopulateExcelDoc_PreRound3****
         └─ **End Loop**
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.