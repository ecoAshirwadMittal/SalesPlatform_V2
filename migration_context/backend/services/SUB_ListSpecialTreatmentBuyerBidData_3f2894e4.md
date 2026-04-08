# Microflow Analysis: SUB_ListSpecialTreatmentBuyerBidData

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (A record of type: AuctionUI.Auction)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$QualifiedBuyerCodesList****
3. **Take the list **$QualifiedBuyerCodesList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode = $BuyerCode }, and call the result **$QualifiedBuyerCodesList_filtered****
4. **Take the list **$QualifiedBuyerCodesList_filtered**, perform a [Head], and call the result **$QualifiedBuyerCode****
5. **Decision:** "Special Treatment?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
