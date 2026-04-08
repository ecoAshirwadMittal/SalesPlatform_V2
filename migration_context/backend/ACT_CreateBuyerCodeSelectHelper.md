# Microflow Detailed Specification: ACT_CreateBuyerCodeSelectHelper

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Call Microflow **AuctionUI.ACT_GetMostRecentAuction** (Result: **$Auction**)**
3. **Call Microflow **AuctionUI.ACT_GetActiveSchedulingAuction** (Result: **$CurrentStartedRound**)**
4. **Call Microflow **AuctionUI.ACT_HandleSingleBuyerCodeLogin** (Result: **$SingleBuyerCodeNotAuction**)**
5. 🔀 **DECISION:** `$SingleBuyerCodeNotAuction`
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_BuyerManagement.Buyer** Filter: `[(EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser/Name = $currentUser/Name and Status = 'Active')]` (Result: **$BuyerList**)**
      2. **DB Retrieve **EcoATM_BuyerManagement.BuyerCodeSelect_Helper**  (Result: **$BuyerCodeSelect_HelperList**)**
      3. **Delete**
      4. **DB Retrieve **System.UserRole** Filter: `[System.UserRoles = $currentUser and (Name='SalesOps' or Name='Administrator')]` (Result: **$SalesOpsUserRole**)**
      5. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_BuyerCodeSelectHelperNP**)**
      6. **CreateList**
      7. 🔀 **DECISION:** `$SalesOpsUserRole = empty`
         ➔ **If [true]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
               │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
               │ 2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe or $currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale or $currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Premium_Wholesale` (Result: **$AuctionBuyerCodeList**)**
               │ 3. 🔀 **DECISION:** `$AuctionBuyerCodeList!=empty`
               │    ➔ **If [true]:**
               │       1. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$AuctionBuyerCodeList**
               │          │ 1. **AggregateList**
               │          │ 2. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$UpdateBuyerSelectHelper**)
      - Set **CompanyName** = `$IteratorBuyer/CompanyName`
      - Set **Code** = `$IteratorBuyerCode/Code`
      - Set **NP_BuyerCodeSelect_Helper_BuyerCode** = `$IteratorBuyerCode`
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_BuyerCodeSelectHelperNP`**
               │          │ 3. **Add **$$UpdateBuyerSelectHelper** to/from list **$BuyerSelect_HelperList_NP****
               │          └─ **End Loop**
               │    ➔ **If [false]:**
               └─ **End Loop**
            3. **Call Microflow **AuctionUI.SUB_CreateBuyerCodeSelectHelper** (Result: **$BuyerCodeSelect_HelperList_2**)**
            4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active' and (BuyerCodeType = 'Data_Wipe' or BuyerCodeType != 'Wholesale') ) ]` (Result: **$SalesOpsBuyerCodeList**)**
            3. 🔄 **LOOP:** For each **$IteratorSalesOpsBuyerCode** in **$SalesOpsBuyerCodeList**
               │ 1. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$SalesOpsBuyerSelectHelper**)
      - Set **CompanyName** = `$IteratorSalesOpsBuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`
      - Set **Code** = `$IteratorSalesOpsBuyerCode/Code`
      - Set **NP_BuyerCodeSelect_Helper_BuyerCode** = `$IteratorSalesOpsBuyerCode`
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_BuyerCodeSelectHelperNP`**
               │ 2. **Add **$$SalesOpsBuyerSelectHelper** to/from list **$BuyerSelect_HelperList_NP****
               └─ **End Loop**
            4. **Call Microflow **AuctionUI.SUB_CreateBuyerCodeSelectHelper** (Result: **$BuyerCodeSelect_HelperList_3**)**
            5. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.