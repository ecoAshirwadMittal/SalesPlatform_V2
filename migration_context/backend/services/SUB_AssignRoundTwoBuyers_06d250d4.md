# Microflow Analysis: SUB_AssignRoundTwoBuyers

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
5. **Decision:** "True?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
7. **Take the list **$SchedulingAuctionList**, perform a [FindByExpression] where: { $currentObject/Round = 2 }, and call the result **$SchedulingAuction****
8. **Run another process: "AuctionUI.SUB_GenerateRound2QualifiedBuyerCodes"
      - Store the result in a new variable called **$BuyerCodeList**** ⚠️ *(This step has a safety catch if it fails)*
9. **Permanently save **$undefined** to the database.**
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
