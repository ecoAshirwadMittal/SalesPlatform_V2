# Microflow Analysis: ACT_Round3_SubmitBidData

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "AuctionUI.ACT_CreateBidSubmitLog"
      - Store the result in a new variable called **$BidSubmitLog****
3. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
4. **Run another process: "EcoATM_BuyerManagement.ACT_GetSubmittedBidRounds"
      - Store the result in a new variable called **$BidderRouterHelper****
5. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
6. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound = $BidRound]
 } (Call this list **$BidDataList**)**
7. **Take the list **$BidDataList**, perform a [FilterByExpression] where: { ($currentObject/BidAmount != empty and $currentObject/BidAmount>0) or ($currentObject/SubmittedBidAmount != empty and $currentObject/SubmittedBidAmount>0) }, and call the result **$ExcludeEmptyBidDataList****
8. **Create List
      - Store the result in a new variable called **$BidDataToCommit****
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Permanently save **$undefined** to the database.**
11. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidRound.Submitted] to: "true"
      - Change [AuctionUI.BidRound.SubmittedDatatime] to: "[%CurrentDateTime%]"
      - **Save:** This change will be saved to the database immediately.**
12. **Run another process: "EcoATM_BuyerManagement.ACT_BidDataDoc_PopulateExcelDoc"** ⚠️ *(This step has a safety catch if it fails)*
13. **Run another process: "EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint"**
14. **Run another process: "AuctionUI.SUB_SendSubmitBidConfirmationEmail"**
15. **Decision:** "send to snowflake?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
16. **Run another process: "AuctionUI.SUB_SendBidDataToSnowflake"**
17. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
18. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
