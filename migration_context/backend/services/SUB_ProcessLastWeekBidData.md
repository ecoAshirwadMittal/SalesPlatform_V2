# Microflow Detailed Specification: SUB_ProcessLastWeekBidData

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Last_Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CarryOverBidsOQLQueryAndProcessing'`**
2. **Create Variable **$Description** = `'Executing OQL query and processing data for CarryOver for Buyer Code : '+$NP_BuyerCodeSelect_Helper/Code`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Create Variable **$OQLQuery_BidData** = `'select EcoID as EcoID, Merged_Grade as Merged_Grade , Max(BidAmount) as Amount, BidQuantity as Quantity, TargetPrice as TargetPrice,MaximumQuantity as MaximumQuantity from AuctionUI."BidData" where AuctionUI.BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted = true and BidAmount > 0 and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code='''+$NP_BuyerCodeSelect_Helper/Code+ ''' and AuctionUI.BidData/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM."Week"/WeekID = ' + toString($Last_Week/WeekID) + ' group by EcoID , Merged_Grade, BidQuantity, TargetPrice, MaximumQuantity'`**
5. **JavaCallAction**
6. **Call Microflow **EcoATM_BidData.SUB_CopyCarryOverBidData** (Result: **$BidDataList**)**
7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
8. 🏁 **END:** Return `$BidDataList`

**Final Result:** This process concludes by returning a [List] value.