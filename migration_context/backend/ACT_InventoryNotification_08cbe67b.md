# Microflow Analysis: ACT_InventoryNotification

### Execution Steps:
1. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [(RoundStatus ='Started') 
and (HasRound = true)
and ((Start_DateTime <= '[%CurrentDateTime%]') or (End_DateTime<='[%CurrentDateTime%]')) ]
 } (Call this list **$SchedulingAuctionList**)**
2. **Run another process: "AuctionUI.ACT_GetTimeOffset"
      - Store the result in a new variable called **$TimeZoneOffset****
3. **Create Variable**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
