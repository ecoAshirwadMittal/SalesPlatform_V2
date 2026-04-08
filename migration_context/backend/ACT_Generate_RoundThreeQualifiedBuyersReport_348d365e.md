# Microflow Analysis: ACT_Generate_RoundThreeQualifiedBuyersReport

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Create List
      - Store the result in a new variable called **$RoundThreeBuyersDataReportList****
4. **Run another process: "AuctionUI.SUB_ListRoundThreeBuyersDataForQualifiedBuyers"
      - Store the result in a new variable called **$RoundThreeBuyersDataReport_NPList****
5. **Search the Database for **AuctionUI.RoundThreeBuyersDataReport** using filter: { [
  (
    AuctionUI.RoundThreeBuyersDataReport_Auction = $Auction
  )
] } (Call this list **$RoundThreeBuyersDataReportList_existing**)**
6. **Delete**
7. **Create Variable**
8. **Java Action Call
      - Store the result in a new variable called **$RoundThreeBuyersData**** ⚠️ *(This step has a safety catch if it fails)*
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Take the list **$RoundThreeBuyersDataReportList**, perform a [FilterByExpression] where: { $currentObject/changedDate<$ProcessBeginTime }, and call the result **$StaleBuyers****
11. **Delete**
12. **Permanently save **$undefined** to the database.**
13. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
