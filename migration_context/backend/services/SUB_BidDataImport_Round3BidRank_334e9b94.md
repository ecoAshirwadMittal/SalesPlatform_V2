# Microflow Analysis: SUB_BidDataImport_Round3BidRank

### Requirements (Inputs):
- **$ImportFile** (A record of type: Custom_Excel_Import.ImportFile)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$BidUploadPageHelper** (A record of type: AuctionUI.BidUploadPageHelper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
2. **Run another process: "EcoATM_BidData.SUB_BidDataImport_GetValidBidData_Round3BidRank"
      - Store the result in a new variable called **$ExcelImport_BidData**** ⚠️ *(This step has a safety catch if it fails)*
3. **Decision:** "Rows imported?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Run another process: "EcoATM_BidData.SUB_BidData_Transform_Round3BidRank"
      - Store the result in a new variable called **$BidData_ToCOmmit****
5. **Decision:** "Bid Data list empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Round 2?**
6. **Close Form**
7. **Run another process: "AuctionUI.SUB_AuctionTimerHelper"
      - Store the result in a new variable called **$AuctionTimerHelper****
8. **Permanently save **$undefined** to the database.**
9. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound = $BidRound and BidAmount > 0] } (Call this list **$BidDataList**)**
10. **Aggregate List
      - Store the result in a new variable called **$BidCount****
11. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidUploadPageHelper.RowCount] to: "$BidCount"
      - Change [AuctionUI.BidUploadPageHelper.FileName] to: "$ImportFile/Name"
      - **Save:** This change will be saved to the database immediately.**
12. **Delete**
13. **Create Object
      - Store the result in a new variable called **$NewBuyerCodeSelect_Helper****
14. **Create Object
      - Store the result in a new variable called **$NewParent_NPBuyerCodeSelectHelper****
15. **Create Object
      - Store the result in a new variable called **$NewNP_BuyerCodeSelect_Helper****
16. **Decision:** "BidRound Exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **?legacy**
17. **Run another process: "Custom_Logging.SUB_Log_Warning"
      - Store the result in a new variable called **$Log****
18. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
