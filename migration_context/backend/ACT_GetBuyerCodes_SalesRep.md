# Microflow Detailed Specification: ACT_GetBuyerCodes_SalesRep

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[RoundStatus='Started']` (Result: **$StartedSchedulingAuction**)**
3. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active' and (BuyerCodeType = 'Data_Wipe' or BuyerCodeType = 'Wholesale') ) ]` (Result: **$AllActiveBuyerCodeList**)**
4. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper**)**
5. **CreateList**
6. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$AllActiveBuyerCodeList**
   │ 1. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **Code** = `$IteratorBuyerCode/Code`
      - Set **CompanyName** = `$IteratorBuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`
      - Set **NP_BuyerCodeSelect_Helper_BuyerCode** = `$IteratorBuyerCode`
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper`
      - Set **comboBoxSearchHelper** = `$IteratorBuyerCode/Code +' '+$IteratorBuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`**
   │ 2. **Add **$$NewNP_BuyerCodeSelect_Helper** to/from list **$NP_BuyerCodeSelect_HelperList_R1R3****
   └─ **End Loop**
7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
8. 🏁 **END:** Return `$NP_BuyerCodeSelect_HelperList_R1R3`

**Final Result:** This process concludes by returning a [List] value.