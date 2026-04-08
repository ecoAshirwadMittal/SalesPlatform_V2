# Microflow Analysis: ACT_DeleteRound3BidDataForBuyer

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Run another process: "AuctionUI.SUB_BidData_BatchDelete"**
3. **Search the Database for **AuctionUI.BidRound** using filter: { [AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction and AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode[EcoATM_BuyerManagement.BuyerCode_Buyer = $Buyer]] } (Call this list **$BidRoundList**)**
4. **Delete**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
