# Microflow Analysis: SUB_UpdateBidRankingDataToSnowflake

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_Info"**
4. **Java Action Call
      - Store the result in a new variable called **$IsSuccess_2_1****
5. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [Auction_Week_Year=$SchedulingAuction/Auction_Week_Year]
[Round=$Round-1]
 } (Call this list **$SchedulingAuction_DB**)**
6. **Execute Database Query
      - Store the result in a new variable called **$numberOfAffectedRows****
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
