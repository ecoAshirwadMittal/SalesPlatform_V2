# Microflow Analysis: ACT_CreateBidDataHelper_wIP

### Requirements (Inputs):
- **$BuyerCodeSelect_Helper** (A record of type: AuctionUI.BuyerCodeSelect_Helper)

### Execution Steps:
1. **Run another process: "AuctionUI.SUB_GetCurrentWeek"
      - Store the result in a new variable called **$Week****
2. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { Show everything } (Call this list **$AgregatedInventoryList**)**
3. **Create Variable**
4. **Java Action Call
      - Store the result in a new variable called **$rows****
5. **Create List
      - Store the result in a new variable called **$BidData_HelperList****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Log Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
