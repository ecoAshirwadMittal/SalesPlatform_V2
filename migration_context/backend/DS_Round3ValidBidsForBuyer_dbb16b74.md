# Microflow Analysis: DS_Round3ValidBidsForBuyer

### Requirements (Inputs):
- **$RoundThreeBuyersData** (A record of type: AuctionUI.RoundThreeBuyersDataReport)
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
3. **Take the list **$SchedulingAuctionList**, perform a [FindByExpression] where: { $currentObject/Round=3 }, and call the result **$Round3SchedulingAuction****
4. **Decision:** "round3 available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Retrieve
      - Store the result in a new variable called **$QualifiedBuyerCodesList****
6. **Take the list **$QualifiedBuyerCodesList**, perform a [FilterByExpression] where: { $currentObject/isSpecialTreatment = true
and $currentObject/Included = true }, and call the result **$QualifiedBuyerCodesList_IncludedSPT****
7. **Create Variable**
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Take the list **$QualifiedBuyerCodesList**, perform a [FilterByExpression] where: { $currentObject/Qualificationtype = EcoATM_BuyerManagement.enum_BuyerCodeQualificationType.Manual
and $currentObject/Included = true
and $currentObject/isSpecialTreatment = false }, and call the result **$QualifiedBuyerCodesList_ManualIncluded****
10. **Create Variable**
11. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
12. **Create Variable**
13. **Java Action Call
      - Store the result in a new variable called **$RoundThreeBuyersBidData**** ⚠️ *(This step has a safety catch if it fails)*
14. **Decision:** "special buyers available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
15. **Create Variable**
16. **Java Action Call
      - Store the result in a new variable called **$RoundThreeBuyersBidData_SpecialBuyers**** ⚠️ *(This step has a safety catch if it fails)*
17. **Take the list **$RoundThreeBuyersBidData**, perform a [Union], and call the result **$RoundThreeBidDataReportList****
18. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
19. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
