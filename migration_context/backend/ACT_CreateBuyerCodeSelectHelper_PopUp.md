# Microflow Detailed Specification: ACT_CreateBuyerCodeSelectHelper_PopUp

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[(RoundStatus = 'Started')]` (Result: **$CurrentStartedRound**)**
2. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$CurrentStartedRound** (Result: **$NP_BuyerCodeSelect_HelperList**)**
3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_HelperList=empty`
   ➔ **If [true]:**
      1. **DB Retrieve **AuctionUI.Buyer** Filter: `[(AuctionUI.EcoATMDirectUser_Buyer/AuctionUI.EcoATMDirectUser/Name = $currentUser/Name and Status = 'Active')]` (Result: **$BuyerList**)**
      2. **DB Retrieve **System.UserRole** Filter: `[System.UserRoles = $currentUser and (Name='SalesOps' or Name='Administrator')]` (Result: **$SalesOpsUserRole**)**
      3. 🔀 **DECISION:** `$CurrentStartedRound/Round = 2`
         ➔ **If [false]:**
            1. **CreateList**
            2. 🔀 **DECISION:** `$SalesOpsUserRole = empty`
               ➔ **If [true]:**
                  1. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
                     │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
                     │ 2. 🔀 **DECISION:** `$BuyerCodeList!=empty`
                     │    ➔ **If [true]:**
                     │       1. **List Operation: **Head** on **$undefined** (Result: **$NewBuyerCode**)**
                     │       2. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
                     │          │ 1. **AggregateList**
                     │          │ 2. **Create **EcoATM_Caching.NP_BuyerCodeSelect_Helper** (Result: **$UpdateBuyerSelectHelper**)
      - Set **CompanyName** = `$IteratorBuyer/CompanyName`
      - Set **Code** = `$IteratorBuyerCode/Code`
      - Set **NP_BuyerCodeSelect_Helper_BuyerCode** = `$IteratorBuyerCode`
      - Set **NP_BuyerCodeSelect_Helper_SchedulingAuction** = `$CurrentStartedRound`
      - Set **NP_BuyerCodeSelect_Helper_SchedulingAuction** = `$CurrentStartedRound`**
                     │          │ 3. **Add **$$UpdateBuyerSelectHelper** to/from list **$BuyerSelect_HelperList****
                     │          └─ **End Loop**
                     │    ➔ **If [false]:**
                     └─ **End Loop**
                  2. **Commit/Save **$BuyerSelect_HelperList** to Database**
                  3. 🏁 **END:** Return `$BuyerSelect_HelperList`
               ➔ **If [false]:**
                  1. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[(AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active')]` (Result: **$SalesOpsBuyerCodeList**)**
                  2. 🔄 **LOOP:** For each **$IteratorSalesOpsBuyerCode** in **$SalesOpsBuyerCodeList**
                     │ 1. **Create **EcoATM_Caching.NP_BuyerCodeSelect_Helper** (Result: **$SalesOpsBuyerSelectHelper**)
      - Set **CompanyName** = `$IteratorSalesOpsBuyerCode/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`
      - Set **Code** = `$IteratorSalesOpsBuyerCode/Code`
      - Set **NP_BuyerCodeSelect_Helper_BuyerCode** = `$IteratorSalesOpsBuyerCode`
      - Set **NP_BuyerCodeSelect_Helper_SchedulingAuction** = `$CurrentStartedRound`
      - Set **NP_BuyerCodeSelect_Helper_SchedulingAuction** = `$CurrentStartedRound`**
                     │ 2. **Add **$$SalesOpsBuyerSelectHelper** to/from list **$BuyerSelect_HelperList****
                     └─ **End Loop**
                  3. **Commit/Save **$BuyerSelect_HelperList** to Database**
                  4. 🏁 **END:** Return `$BuyerSelect_HelperList`
         ➔ **If [true]:**
            1. **Call Microflow **AuctionUI.ACT_Round2BuyerCodes** (Result: **$BuyerCodeList_Round2**)**
            2. **CreateList**
            3. 🔄 **LOOP:** For each **$IteratorBuyerCode_Round2** in **$BuyerCodeList_Round2**
               │ 1. **Create **EcoATM_Caching.NP_BuyerCodeSelect_Helper** (Result: **$UpdateBuyerSelectHelper_Round2**)
      - Set **CompanyName** = `$IteratorBuyerCode_Round2/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`
      - Set **Code** = `$IteratorBuyerCode_Round2/Code`
      - Set **NP_BuyerCodeSelect_Helper_BuyerCode** = `$IteratorBuyerCode_Round2`
      - Set **NP_BuyerCodeSelect_Helper_SchedulingAuction** = `$CurrentStartedRound`
      - Set **NP_BuyerCodeSelect_Helper_SchedulingAuction** = `$CurrentStartedRound`**
               │ 2. **Add **$$UpdateBuyerSelectHelper_Round2** to/from list **$BuyerSelect_HelperList_Round2****
               └─ **End Loop**
            4. **Commit/Save **$BuyerSelect_HelperList_Round2** to Database**
            5. 🏁 **END:** Return `$BuyerSelect_HelperList_Round2`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$NP_BuyerCodeSelect_HelperList`

**Final Result:** This process concludes by returning a [List] value.