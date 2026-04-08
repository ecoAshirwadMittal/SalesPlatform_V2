# Microflow Analysis: SUB_UpdateScheduleAuctionStatus

### Requirements (Inputs):
- **$SchedulingAuctionStatus** (A record of type: Object)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [id = $SchedulingAuction]
 } (Call this list **$SchedulingAuctionToUpdate**)**
3. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction.RoundStatus] to: "$SchedulingAuctionStatus
"
      - **Save:** This change will be saved to the database immediately.**
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
