# Microflow Detailed Specification: DS_Round3ValidBidsForBuyer

### 📥 Inputs (Parameters)
- **$RoundThreeBuyersData** (Type: AuctionUI.RoundThreeBuyersDataReport)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Round=3` (Result: **$Round3SchedulingAuction**)**
4. 🔀 **DECISION:** `$Round3SchedulingAuction!=empty`
   ➔ **If [true]:**
      1. **Retrieve related **QualifiedBuyerCodes_SchedulingAuction** via Association from **$Round3SchedulingAuction** (Result: **$QualifiedBuyerCodesList**)**
      2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/isSpecialTreatment = true and $currentObject/Included = true` (Result: **$QualifiedBuyerCodesList_IncludedSPT**)**
      3. **Create Variable **$SpecialBuyerCodes** = `empty`**
      4. 🔄 **LOOP:** For each **$IteratorQualifiedBuyerCodeSPT** in **$QualifiedBuyerCodesList_IncludedSPT**
         │ 1. **Update Variable **$SpecialBuyerCodes** = `if($SpecialBuyerCodes=empty) then ''''+$IteratorQualifiedBuyerCodeSPT/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code+'''' else $SpecialBuyerCodes +','+''''+$IteratorQualifiedBuyerCodeSPT/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code+''''`**
         └─ **End Loop**
      5. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/Qualificationtype = EcoATM_BuyerManagement.enum_BuyerCodeQualificationType.Manual and $currentObject/Included = true and $currentObject/isSpecialTreatment = false` (Result: **$QualifiedBuyerCodesList_ManualIncluded**)**
      6. **Create Variable **$ManualBuyerCodes** = `empty`**
      7. 🔄 **LOOP:** For each **$IteratorQualifiedBuyerCode** in **$QualifiedBuyerCodesList_ManualIncluded**
         │ 1. **Update Variable **$ManualBuyerCodes** = `if($ManualBuyerCodes=empty) then ''''+$IteratorQualifiedBuyerCode/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code+'''' else $ManualBuyerCodes +','+''''+$IteratorQualifiedBuyerCode/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code+''''`**
         └─ **End Loop**
      8. **Create Variable **$OQLQueryForSubmittedBids** = `' select distinct CodeGrade,Brand,PartName,EstimatedUnitCount,BuyerQtyCap,''$''+ REPLACE(Bid, ''.00'', '''') AS Bid, ''$''+ REPLACE(MaxBid, ''.00'', '''') AS MaxBid ,AcceptMaxBid,BuyerName,BuyerCode,WinningSKU from ( select distinct CAST(bd.EcoID as String) +'' ''+bd.Merged_Grade as CodeGrade, ai.Brand, ai.Name as PartName, bd.MaximumQuantity as EstimatedUnitCount, MAX( CASE WHEN bd.HighestBid = true THEN ''Yes'' ELSE ''No'' END ) AS WinningSKU, Case When bd.BidQuantity = -1 then ''NO QUANTITY LIMIT'' Else cast(bd.BidQuantity as String) End as BuyerQtyCap, cast(round(MAX(bd.BidAmount) ,2) as String) as Bid, cast(round(ai.AvgTargetPrice,2) as String) as MaxBid, '''' as AcceptMaxBid, b.CompanyName as BuyerName, bc.Code as BuyerCode from AuctionUI.BidRound br join br/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction sa join br/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode bc join bc/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer b left join br/AuctionUI.BidRound_SubmittedBy/EcoATM_UserManagement.EcoATMDirectUser edu join br/AuctionUI.BidData_BidRound/AuctionUI.BidData bd join bd/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory ai where 1=1 AND bc.Code not in ('+$SpecialBuyerCodes+') AND ((bd."SubmittedBidAmount" > 0 OR bd."SubmittedBidQuantity" > 0) or bc.Code in ('+$ManualBuyerCodes+')) and sa."Auction_Week_Year"='''+$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekDisplay+''' and b.CompanyName = '''+$RoundThreeBuyersData/CompanyName+''' GROUP BY CodeGrade, ai.Brand, ai.Name, bd.MaximumQuantity, bd.BidQuantity, ai.AvgTargetPrice, b.CompanyName, bc.Code ) as GroupedData order by PartName'`**
      9. **JavaCallAction**
      10. 🔀 **DECISION:** `$SpecialBuyerCodes!=empty`
         ➔ **If [true]:**
            1. **Create Variable **$OQLQueryForSpecialBuyers** = `' select distinct CodeGrade,Brand,PartName,EstimatedUnitCount,BuyerQtyCap,''$''+ REPLACE(Bid, ''.00'', '''')AS Bid, ''$''+ REPLACE(MaxBid, ''.00'', '''') AS MaxBid ,AcceptMaxBid,BuyerName,BuyerCode,WinningSKU from ( select distinct CAST(bd.EcoID as String) +'' ''+bd.Merged_Grade as CodeGrade, ai.Brand, ai.Name as PartName, bd.MaximumQuantity as EstimatedUnitCount, MAX( CASE WHEN bd.HighestBid = true THEN ''Yes'' ELSE ''No'' END ) AS WinningSKU, Case When bd.BidQuantity = -1 then ''NO QUANTITY LIMIT'' Else cast(bd.BidQuantity as String) End as BuyerQtyCap, cast(round(MAX(bd.BidAmount) ,2) as String) as Bid, cast(round(ai.AvgTargetPrice,2) as String) as MaxBid, '''' as AcceptMaxBid, b.CompanyName as BuyerName, bc.Code as BuyerCode from AuctionUI.BidRound br join br/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction sa join br/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode bc join bc/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer b left join br/AuctionUI.BidRound_SubmittedBy/EcoATM_UserManagement.EcoATMDirectUser edu join br/AuctionUI.BidData_BidRound/AuctionUI.BidData bd join bd/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory ai where 1=1 AND bc.Code in ('+$SpecialBuyerCodes+') and sa."Auction_Week_Year"='''+$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekDisplay+''' and b.CompanyName = '''+$RoundThreeBuyersData/CompanyName+''' GROUP BY CodeGrade, ai.Brand, ai.Name, bd.MaximumQuantity, bd.BidQuantity, ai.AvgTargetPrice, b.CompanyName, bc.Code ) as GroupedData order by PartName'`**
            2. **JavaCallAction**
            3. **List Operation: **Union** on **$undefined** (Result: **$RoundThreeBidDataReportList**)**
            4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            5. 🏁 **END:** Return `$RoundThreeBidDataReportList`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. 🏁 **END:** Return `$RoundThreeBuyersBidData`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.