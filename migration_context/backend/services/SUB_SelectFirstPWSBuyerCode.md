# Microflow Detailed Specification: SUB_SelectFirstPWSBuyerCode

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_GET_CurrentUser** (Result: **$EcoATMDirectUser**)**
2. **CreateList**
3. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$EcoATMDirectUser** (Result: **$BuyerList**)**
4. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
   │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList_Buyer**)**
   │ 2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Premium_Wholesale` (Result: **$BuyerCodeList_PWS**)**
   │ 3. **Add **$$BuyerCodeList_PWS
** to/from list **$BuyerCodeList****
   └─ **End Loop**
5. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_BuyerManagement.BuyerCode_Session = empty or $currentObject/EcoATM_BuyerManagement.BuyerCode_Session = $currentSession` (Result: **$BuyerCodeList_Available**)**
6. **List Operation: **Head** on **$undefined** (Result: **$BuyerCode**)**
7. **Update **$BuyerCode** (and Save to DB)
      - Set **BuyerCode_Session** = `$currentSession`**
8. 🏁 **END:** Return `$BuyerCode`

**Final Result:** This process concludes by returning a [Object] value.