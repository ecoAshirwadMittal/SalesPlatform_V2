# Microflow Analysis: ACT_CleanupDataForAWeek

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Retrieve
      - Store the result in a new variable called **$AggreegatedInventoryTotals****
4. **Retrieve
      - Store the result in a new variable called **$Auction****
5. **Retrieve
      - Store the result in a new variable called **$BidRoundList****
6. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
7. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
8. **Create Object
      - Store the result in a new variable called **$NewBidDataDeleteHelper****
9. **Run another process: "AuctionUI.MF_CleanupUsingStoredProcedure"** ⚠️ *(This step has a safety catch if it fails)*
10. **Delete**
11. **Delete**
12. **Delete**
13. **Delete**
14. **Delete**
15. **Update the **$undefined** (Object):
      - Change [EcoATM_MDM.Week.AuctionDataPurged] to: "true
"
      - **Save:** This change will be saved to the database immediately.**
16. **Show Message**
17. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
18. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
