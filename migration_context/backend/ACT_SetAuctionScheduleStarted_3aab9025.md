# Microflow Analysis: ACT_SetAuctionScheduleStarted

### Execution Steps:
1. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [(RoundStatus = 'Scheduled')]
 } (Call this list **$SchedulingAuctionList_ToBeStarted**)**
2. **Run another process: "AuctionUI.ACT_GetTimeOffset"
      - Store the result in a new variable called **$TimeZoneOffset****
3. **Create Variable**
4. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
