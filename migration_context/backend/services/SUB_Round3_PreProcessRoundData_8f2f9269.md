# Microflow Analysis: SUB_Round3_PreProcessRoundData

### Requirements (Inputs):
- **$Round2SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "AuctionUI.SUB_Round2_DeleteUnsubmittedBids"**
2. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction=$Round2SchedulingAuction/AuctionUI.SchedulingAuction_Auction]
[Round = 3] } (Call this list **$Round3SchedulingAuction**)**
3. **Decision:** "Active?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Run another process: "AuctionUI.SUB_InitializeRound3"**
5. **Run another process: "AuctionUI.SUB_GenerateRound3QualifiedBuyerCodes"
      - Store the result in a new variable called **$BuyerCodeList****
6. **Run another process: "AuctionUI.ACT_Generate_RoundThreeQualifiedBuyersReport"
      - Store the result in a new variable called **$RoundThreeBuyersDataReportList****
7. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction_QualifiedBuyers] to: "$BuyerCodeList"
      - **Save:** This change will be saved to the database immediately.**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
