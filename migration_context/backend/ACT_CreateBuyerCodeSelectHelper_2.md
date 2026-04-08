# Microflow Detailed Specification: ACT_CreateBuyerCodeSelectHelper_2

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[(Status = 'Started')]` (Result: **$CurrentStartedRound**)**
2. **DB Retrieve **AuctionUI.Buyer** Filter: `[(AuctionUI.EcoATMDirectUser_Buyer/AuctionUI.EcoATMDirectUser/Name = $currentUser/Name and Status = 'Active')]` (Result: **$BuyerList**)**
3. **CreateList**
4. **DB Retrieve **System.UserRole** Filter: `[System.UserRoles = $currentUser and (Name='SalesOps' or Name='Administrator')]` (Result: **$SalesOpsUserRole**)**
5. 🔀 **DECISION:** `$SalesOpsUserRole = empty`
   ➔ **If [true]:**
      1. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
         │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
         │ 2. 🔀 **DECISION:** `$BuyerCodeList!=empty`
         │    ➔ **If [true]:**
         │       1. **List Operation: **Head** on **$undefined** (Result: **$NewBuyerCode**)**
         │       2. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
         │          │ 1. **AggregateList**
         │          │ 2. **Create **AuctionUI.BuyerCodeSelect_Helper** (Result: **$UpdateBuyerSelectHelper**)
      - Set **CompanyName** = `$IteratorBuyer/CompanyName`
      - Set **Code** = `$IteratorBuyerCode/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$IteratorBuyerCode`
      - Set **BuyerCodeSelect_Helper_SchedulingAuction** = `$CurrentStartedRound`**
         │          │ 3. **Add **$$UpdateBuyerSelectHelper** to/from list **$BuyerSelect_HelperList****
         │          └─ **End Loop**
         │    ➔ **If [false]:**
         └─ **End Loop**
      2. 🏁 **END:** Return `$BuyerSelect_HelperList`
   ➔ **If [false]:**
      1. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[(AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active')]` (Result: **$SalesOpsBuyerCodeList**)**
      2. 🔄 **LOOP:** For each **$IteratorSalesOpsBuyerCode** in **$SalesOpsBuyerCodeList**
         │ 1. **Create **AuctionUI.BuyerCodeSelect_Helper** (Result: **$SalesOpsBuyerSelectHelper**)
      - Set **CompanyName** = `$IteratorSalesOpsBuyerCode/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`
      - Set **Code** = `$IteratorSalesOpsBuyerCode/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$IteratorSalesOpsBuyerCode`
      - Set **BuyerCodeSelect_Helper_SchedulingAuction** = `$CurrentStartedRound`**
         │ 2. **Add **$$SalesOpsBuyerSelectHelper** to/from list **$BuyerSelect_HelperList****
         └─ **End Loop**
      3. 🏁 **END:** Return `$BuyerSelect_HelperList`

**Final Result:** This process concludes by returning a [List] value.