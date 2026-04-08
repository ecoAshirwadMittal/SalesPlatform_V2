# Microflow Analysis: ACT_SetAuctionScheduleClosed

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_GetTimeOffset"
      - Store the result in a new variable called **$TimeZoneOffset****
2. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [RoundStatus = 'Started']
 } (Call this list **$SchedulingAuctionList_NotClosed**)**
3. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Permanently save **$undefined** to the database.**
6. **Log Message**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
