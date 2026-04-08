# Microflow Detailed Specification: SUB_ListRoundThreeBuyersDataForQualifiedBuyers

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
2. **List Operation: **Filter** on **$undefined** where `3` (Result: **$SchedulingAuctionList_Round3**)**
3. **List Operation: **Head** on **$undefined** (Result: **$SchedulingAuction_Round3**)**
4. **Retrieve related **QualifiedBuyerCodes_SchedulingAuction** via Association from **$SchedulingAuction_Round3** (Result: **$QualifiedBuyerCodesList**)**
5. **List Operation: **Filter** on **$undefined** where `true` (Result: **$QualifiedBuyerCodesList_Included**)**
6. **CreateList**
7. 🔄 **LOOP:** For each **$IteratorQualifiedBuyerCodes** in **$QualifiedBuyerCodesList_Included**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorQualifiedBuyerCodes/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName` (Result: **$RoundThreeBuyersDataReport_existing**)**
   │ 2. 🔀 **DECISION:** `$RoundThreeBuyersDataReport_existing != empty`
   │    ➔ **If [true]:**
   │       1. **Update **$RoundThreeBuyersDataReport_existing**
      - Set **BuyerCodes** = `$RoundThreeBuyersDataReport_existing/BuyerCodes + ',' + $IteratorQualifiedBuyerCodes/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code`**
   │    ➔ **If [false]:**
   │       1. **Create **AuctionUI.RoundThreeBuyersDataReport_NP** (Result: **$NewRoundThreeBuyersDataReport**)
      - Set **CompanyName** = `$IteratorQualifiedBuyerCodes/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`
      - Set **BuyerCodes** = `$IteratorQualifiedBuyerCodes/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code`**
   │       2. **Add **$$NewRoundThreeBuyersDataReport** to/from list **$RoundThreeBuyersDataReport_NP****
   └─ **End Loop**
8. 🏁 **END:** Return `$RoundThreeBuyersDataReport_NP`

**Final Result:** This process concludes by returning a [List] value.