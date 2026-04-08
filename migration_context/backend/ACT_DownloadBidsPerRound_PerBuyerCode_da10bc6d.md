# Microflow Analysis: ACT_DownloadBidsPerRound_PerBuyerCode

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BuyerCode** (A record of type: AuctionUI.BuyerCode)
- **$FinalBuyerCode** (A record of type: Object)

### Execution Steps:
1. **Log Message**
2. **Search the Database for **AuctionUI.AllBidDownload_ScreenHelper** using filter: { Show everything } (Call this list **$AllBidDownload_ScreenHelper**)**
3. **Update the **$undefined** (Object):
      - Change [AuctionUI.AllBidDownload_ScreenHelper.R1Caption] to: "if $SchedulingAuction/Round=1 then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R1Caption"
      - Change [AuctionUI.AllBidDownload_ScreenHelper.R2Caption] to: "if $SchedulingAuction/Round=2 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R2Caption"
      - Change [AuctionUI.AllBidDownload_ScreenHelper.UpsellCaption] to: "if $SchedulingAuction/Round=3 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/UpsellCaption"**
4. **Java Action Call
      - Store the result in a new variable called **$Variable****
5. **Update the **$undefined** (Object):
      - Change [AuctionUI.AllBidDownload_ScreenHelper.R1Caption] to: "if $SchedulingAuction/Round=1 then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R1Caption"
      - Change [AuctionUI.AllBidDownload_ScreenHelper.R2Caption] to: "if $SchedulingAuction/Round=2 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R2Caption"
      - Change [AuctionUI.AllBidDownload_ScreenHelper.UpsellCaption] to: "if $SchedulingAuction/Round=3 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/UpsellCaption"**
6. **Retrieve
      - Store the result in a new variable called **$Auction****
7. **Retrieve
      - Store the result in a new variable called **$Week****
8. **Create List
      - Store the result in a new variable called **$BuyerCodeList****
9. **Change List**
10. **Create List
      - Store the result in a new variable called **$AllBidsTempList****
11. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='AllBids_by_BuyerCode'] } (Call this list **$MxTemplate**)**
12. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
13. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [
AuctionUI.AggregatedInventory_Week=$Week and
Data_Wipe_Quantity > 0
] } (Call this list **$AgregatedInventoryList_DataWipe**)**
14. **Create List
      - Store the result in a new variable called **$AllBidDownloadList_2****
15. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
16. **Delete**
17. **Decision:** "Final Buyer Code?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
18. **Update the **$undefined** (Object):
      - Change [AuctionUI.AllBidDownload_ScreenHelper.R1Caption] to: "if $SchedulingAuction/Round=1 then 'Click here for Bid Files' else $AllBidDownload_ScreenHelper/R1Caption"
      - Change [AuctionUI.AllBidDownload_ScreenHelper.R2Caption] to: "if $SchedulingAuction/Round=2 and $SchedulingAuction/IsActive then 'Click here for Bid Files' else $AllBidDownload_ScreenHelper/R2Caption"
      - Change [AuctionUI.AllBidDownload_ScreenHelper.UpsellCaption] to: "if $SchedulingAuction/Round=3 and $SchedulingAuction/IsActive then 'Click here for Bid Files' else $AllBidDownload_ScreenHelper/UpsellCaption"**
19. **Log Message**
20. **Permanently save **$undefined** to the database.**
21. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
