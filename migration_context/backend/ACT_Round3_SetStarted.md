# Microflow Detailed Specification: ACT_Round3_SetStarted

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Update **$SchedulingAuction** (and Save to DB)
      - Set **RoundStatus** = `AuctionUI.enum_SchedulingAuctionStatus.Started`
      - Set **Round3InitStatus** = `AuctionUI.Enum_ScheduleAuctionInitStatus.Complete`**
3. **Call Microflow **AuctionUI.ACT_Round3_StartNotification****
4. **Call Microflow **AuctionUI.SUB_Round3SendAuctionToSnowflake****
5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.