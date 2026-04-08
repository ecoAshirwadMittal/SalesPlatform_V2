# Microflow Analysis: ACT_RemoveRound2BuyerCode

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
3. **Take the list **$SchedulingAuctionList**, perform a [Find] where: { 2 }, and call the result **$SchedulingAuction****
4. **Retrieve
      - Store the result in a new variable called **$BuyerCodeList****
5. **Change List**
6. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction_QualifiedBuyers] to: "$BuyerCodeList"**
7. **Permanently save **$undefined** to the database.**
8. **Update the **$undefined** (Object):
      - **Save:** This change will be saved to the database immediately.**
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
