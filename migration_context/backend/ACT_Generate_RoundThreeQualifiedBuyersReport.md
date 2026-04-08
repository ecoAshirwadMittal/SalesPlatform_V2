# Microflow Detailed Specification: ACT_Generate_RoundThreeQualifiedBuyersReport

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$ProcessBeginTime** = `[%CurrentDateTime%]`**
2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
3. **CreateList**
4. **Call Microflow **AuctionUI.SUB_ListRoundThreeBuyersDataForQualifiedBuyers** (Result: **$RoundThreeBuyersDataReport_NPList**)**
5. **DB Retrieve **AuctionUI.RoundThreeBuyersDataReport** Filter: `[ ( AuctionUI.RoundThreeBuyersDataReport_Auction = $Auction ) ]` (Result: **$RoundThreeBuyersDataReportList_existing**)**
6. **Delete**
7. **Create Variable **$OQLQuery** = `'select CompanyName, string_agg(BuyerCode, '', '') as BuyerCodes, SubmittedBy, MAX(SubmittedOn) as SubmittedOn from ( select distinct b.CompanyName, bc.Code as BuyerCode, edu.FullName as SubmittedBy, MIN(br.SubmittedDatatime) as SubmittedOn from AuctionUI.BidRound br join br/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction sa join br/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode bc join bc/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer b left join br/AuctionUI.BidRound_SubmittedBy/EcoATM_UserManagement.EcoATMDirectUser edu where 1=1 and (br."Submitted"=true or b."isSpecialBuyer" = true) and sa."Auction_Week_Year"='''+$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekDisplay+''' group by b.CompanyName, edu.FullName, bc.Code ) inner_query group by CompanyName, SubmittedBy'`**
8. **JavaCallAction**
9. 🔄 **LOOP:** For each **$IteratorRoundThreeBuyersDataReport** in **$RoundThreeBuyersDataReport_NPList**
   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/CompanyName=$IteratorRoundThreeBuyersDataReport/CompanyName` (Result: **$ReportForBuyerExists**)**
   │ 2. 🔀 **DECISION:** `$ReportForBuyerExists!=empty`
   │    ➔ **If [false]:**
   │       1. **Create **AuctionUI.RoundThreeBuyersDataReport** (Result: **$NewRoundThreeBuyersDataReport**)
      - Set **CompanyName** = `$IteratorRoundThreeBuyersDataReport/CompanyName`
      - Set **BuyerCodes** = `$IteratorRoundThreeBuyersDataReport/BuyerCodes`
      - Set **RoundThreeBuyersDataReport_Auction** = `$Auction`**
   │       2. **Add **$$NewRoundThreeBuyersDataReport** to/from list **$RoundThreeBuyersDataReportList****
   │    ➔ **If [true]:**
   │       1. **Update **$ReportForBuyerExists**
      - Set **CompanyName** = `$IteratorRoundThreeBuyersDataReport/CompanyName`
      - Set **BuyerCodes** = `$IteratorRoundThreeBuyersDataReport/BuyerCodes`
      - Set **RoundThreeBuyersDataReport_Auction** = `$Auction`**
   │       2. **Call Microflow **Custom_Logging.SUB_Log_Info****
   │       3. **Add **$$ReportForBuyerExists** to/from list **$RoundThreeBuyersDataReportList****
   └─ **End Loop**
10. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/changedDate<$ProcessBeginTime` (Result: **$StaleBuyers**)**
11. **Delete**
12. **Commit/Save **$RoundThreeBuyersDataReportList** to Database**
13. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
14. 🏁 **END:** Return `$RoundThreeBuyersDataReportList`

**Final Result:** This process concludes by returning a [List] value.