# Microflow Analysis: ACT_MAPAuctionsWeekDA_Admin

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Search the Database for **AuctionUI.Auction** using filter: { Show everything } (Call this list **$AuctionList**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
