# Microflow Detailed Specification: DS_PWS_BuyerCodes

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSGetBuyerCodes'`**
2. **Create Variable **$Description** = `'Get PWS Buyer Codes'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **AuctionUI.ACT_GetCurrentUser** (Result: **$EcoATMDirectUser**)**
5. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
6. **List Operation: **Find** on **$undefined** where `'Bidder'` (Result: **$BidderUserRoleExists**)**
7. **CreateList**
8. 🔀 **DECISION:** `$BidderUserRoleExists!=empty`
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[Status='Active'] [EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'] [BuyerCodeType = 'Premium_Wholesale']` (Result: **$AllActiveBuyerCodeList**)**
      2. **Add **$$AllActiveBuyerCodeList
** to/from list **$BuyerCodeList****
      3. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper**)**
      4. **CreateList**
      5. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
         │ 1. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **Code** = `$IteratorBuyerCode/Code`
      - Set **CompanyName** = `$IteratorBuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`
      - Set **NP_BuyerCodeSelect_Helper_BuyerCode** = `$IteratorBuyerCode`
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper`
      - Set **comboBoxSearchHelper** = `$IteratorBuyerCode/Code +' '+$IteratorBuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`**
         │ 2. **Add **$$NewNP_BuyerCodeSelect_Helper** to/from list **$NP_BuyerCodeSelect_HelperList_R1R3****
         └─ **End Loop**
      6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      7. 🏁 **END:** Return `$NP_BuyerCodeSelect_HelperList_R1R3`
   ➔ **If [true]:**
      1. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$EcoATMDirectUser** (Result: **$BuyerList**)**
      2. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
         │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList_Buyer**)**
         │ 2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BuyerCodeType != AuctionUI.enum_BuyerCodeType.Purchasing_Order and $currentObject/BuyerCodeType != AuctionUI.enum_BuyerCodeType.Purchasing_Order_Data_Wipe` (Result: **$BuyerCodeList_NonPO**)**
         │ 3. **Add **$$BuyerCodeList_NonPO
** to/from list **$BuyerCodeList****
         └─ **End Loop**
      3. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper**)**
      4. **CreateList**
      5. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
         │ 1. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **Code** = `$IteratorBuyerCode/Code`
      - Set **CompanyName** = `$IteratorBuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`
      - Set **NP_BuyerCodeSelect_Helper_BuyerCode** = `$IteratorBuyerCode`
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper`
      - Set **comboBoxSearchHelper** = `$IteratorBuyerCode/Code +' '+$IteratorBuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`**
         │ 2. **Add **$$NewNP_BuyerCodeSelect_Helper** to/from list **$NP_BuyerCodeSelect_HelperList_R1R3****
         └─ **End Loop**
      6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      7. 🏁 **END:** Return `$NP_BuyerCodeSelect_HelperList_R1R3`

**Final Result:** This process concludes by returning a [List] value.