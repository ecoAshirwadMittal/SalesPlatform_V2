# Microflow Analysis: ACT_SubmitBidData

### Requirements (Inputs):
- **$BuyerCodeSelect_Helper_NP** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$AuctionTimerHelper** (A record of type: AuctionUI.AuctionTimerHelper)
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [Name = $currentUser/Name]
 } (Call this list **$EcoATMDirectUserList**)**
3. **Create Variable**
4. **Decision:** "Not Bidder?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Change Variable**
6. **Close Form**
7. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
8. **Retrieve
      - Store the result in a new variable called **$BidRound****
9. **Run another process: "AuctionUI.ACT_CreateBidSubmitLog"
      - Store the result in a new variable called **$BidSubmitLog****
10. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
11. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound = $BidRound]
 } (Call this list **$BidDataList**)**
12. **Take the list **$BidDataList**, perform a [FilterByExpression] where: { ($currentObject/BidAmount != empty and $currentObject/BidAmount>0) or ($currentObject/SubmittedBidAmount != empty and $currentObject/SubmittedBidAmount>0) }, and call the result **$ExcludeEmptyBidDataList****
13. **Take the list **$BidDataList**, perform a [FilterByExpression] where: { ($currentObject/BidAmount != empty and $currentObject/BidAmount < $BuyerCodeSubmitConfig/MinimumAllowedBid)
and ($currentObject/BidAmount != empty and $currentObject/BidAmount > 0) }, and call the result **$BidDataList_BelowMinBid_nonzero****
14. **Aggregate List
      - Store the result in a new variable called **$BidsBelowMinBidConfig****
15. **Create List
      - Store the result in a new variable called **$BidDataToCommit****
16. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
17. **Permanently save **$undefined** to the database.**
18. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidRound.Submitted] to: "true"
      - Change [AuctionUI.BidRound.Note] to: "$BuyerCodeSelect_Helper_NP/Note"
      - Change [AuctionUI.BidRound_SchedulingAuction] to: "$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_SchedulingAuction"
      - Change [AuctionUI.BidRound_BuyerCode] to: "$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode"
      - Change [AuctionUI.BidRound.SubmittedDatatime] to: "[%CurrentDateTime%]"
      - **Save:** This change will be saved to the database immediately.**
19. **Decision:** "Execute?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
20. **Run another process: "EcoATM_BuyerManagement.ACT_BidDataDoc_PopulateExcelDoc"** ⚠️ *(This step has a safety catch if it fails)*
21. **Decision:** "?execute"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
22. **Run another process: "EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint"**
23. **Create Object
      - Store the result in a new variable called **$NewBuyerCodeSelect_Helper****
24. **Run another process: "AuctionUI.SUB_SendSubmitBidConfirmationEmail"**
25. **Decision:** "send to snowflake?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
26. **Run another process: "AuctionUI.SUB_SendBidDataToSnowflake"**
27. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
28. **Create Object
      - Store the result in a new variable called **$BidSubmitConfirmationHelper****
29. **Show Page**
30. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
