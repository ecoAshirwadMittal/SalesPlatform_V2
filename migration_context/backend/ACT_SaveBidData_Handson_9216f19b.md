# Microflow Analysis: ACT_SaveBidData_Handson

### Requirements (Inputs):
- **$BidData_HelperList** (A record of type: AuctionUI.BidData_Helper)

### Execution Steps:
1. **Decision:** "BidData_HelperList has items?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Run another process: "AuctionUI.SUB_GetCurrentWeek"
      - Store the result in a new variable called **$Week****
3. **Retrieve
      - Store the result in a new variable called **$Auction****
4. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
5. **Run another process: "AuctionUI.ACT_GetActiveSchedulingAuction"
      - Store the result in a new variable called **$CurrentSchedulingAuction****
6. **Take the list **$SchedulingAuctionList**, perform a [Filter] where: { AuctionUI.enum_SchedulingAuctionStatus.Started }, and call the result **$NewSchedulingAuctionList****
7. **Take the list **$NewSchedulingAuctionList**, perform a [Head], and call the result **$CurrentSchedulingAuction_1****
8. **Take the list **$BidData_HelperList**, perform a [Head], and call the result **$FirstBidData****
9. **Search the Database for **AuctionUI.BidRound** using filter: { [AuctionUI.BidRound_SchedulingAuction=$CurrentSchedulingAuction and AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$FirstBidData/Code] } (Call this list **$BidRound**)**
10. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [Code=$FirstBidData/Code] } (Call this list **$BuyerCode**)**
11. **Search the Database for **AuctionUI.BidData** using filter: { [Code = $BuyerCode/Code]
[AuctionUI.BidData_BidRound = $BidRound] } (Call this list **$Existing_BidDataList**)**
12. **Create List
      - Store the result in a new variable called **$BidDataList_ToCOmmit****
13. **Create Variable**
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Permanently save **$undefined** to the database.**
16. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
