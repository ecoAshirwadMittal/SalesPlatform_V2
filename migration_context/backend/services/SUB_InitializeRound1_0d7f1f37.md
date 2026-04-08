# Microflow Analysis: SUB_InitializeRound1

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [
  (
    EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'
  )
  and
  (
    BuyerCodeType = 'Wholesale'
    or BuyerCodeType = 'Data_Wipe'
  )
] } (Call this list **$BuyerCodeList**)**
2. **Run another process: "AuctionUI.ACT_UpdateRound1TargetPrice_MinBid"**
3. **Run another process: "AuctionUI.SUB_CreateQualifiedBuyersEntity"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
