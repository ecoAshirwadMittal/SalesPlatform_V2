# Microflow Analysis: SUB_CheckRoundIncluded

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$Buyercode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Retrieve
      - Store the result in a new variable called **$QualifiedBuyerCodeList****
5. **Take the list **$QualifiedBuyerCodeList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode = $Buyercode
and
$currentObject/Included = true }, and call the result **$QualifiedBuyerCode****
6. **Aggregate List
      - Store the result in a new variable called **$CountIncluded****
7. **Decision:** ">0?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
8. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
