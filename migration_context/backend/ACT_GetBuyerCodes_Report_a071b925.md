# Microflow Analysis: ACT_GetBuyerCodes_Report

### Execution Steps:
1. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [RoundStatus='Started'] } (Call this list **$StartedSchedulingAuction**)**
2. **Decision:** "Round 2?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Search the Database for **AuctionUI.BuyerCode** using filter: { [AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active'] } (Call this list **$AllActiveBuyerCodeList**)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
