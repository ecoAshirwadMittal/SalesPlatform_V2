# Microflow Analysis: ACT_Round3_SetStarted

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction.RoundStatus] to: "AuctionUI.enum_SchedulingAuctionStatus.Started
"
      - Change [AuctionUI.SchedulingAuction.Round3InitStatus] to: "AuctionUI.Enum_ScheduleAuctionInitStatus.Complete"
      - **Save:** This change will be saved to the database immediately.**
3. **Run another process: "AuctionUI.ACT_Round3_StartNotification"** ⚠️ *(This step has a safety catch if it fails)*
4. **Run another process: "AuctionUI.SUB_Round3SendAuctionToSnowflake"** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
