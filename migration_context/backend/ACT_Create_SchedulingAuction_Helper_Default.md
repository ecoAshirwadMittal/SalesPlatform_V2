# Microflow Detailed Specification: ACT_Create_SchedulingAuction_Helper_Default

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_GetTimeOffset** (Result: **$TimezoneOffset**)**
2. **Create Variable **$StartHourOffset** = `$TimezoneOffset+11`**
3. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
4. **Update **$SchedulingAuction_Helper**
      - Set **Round1_Start_DateTime** = `addHours($Week/WeekStartDateTime,$StartHourOffset)`
      - Set **Round1_End_DateTime** = `addHours($Week/WeekStartDateTime,$StartHourOffset+87)`
      - Set **Round2_Start_DateTime** = `addMinutes($Week/WeekStartDateTime,60*($StartHourOffset+87)+ $BuyerCodeSubmitConfig/AuctionRound2MinutesOffset)`
      - Set **Round2_End_DateTime** = `addHours($Week/WeekStartDateTime,$StartHourOffset+112)`
      - Set **Round3_Start_DateTime** = `addMinutes($Week/WeekStartDateTime,60*($StartHourOffset+112)+$BuyerCodeSubmitConfig/AuctionRound3MinutesOffset)`
      - Set **Round3_End_Datetime** = `addHours($Week/WeekStartDateTime,$StartHourOffset+140)`
      - Set **SchedulingAuction_Helper_Auction** = `$Week/AuctionUI.Auction_Week/AuctionUI.Auction`
      - Set **Round1_Status** = `AuctionUI.enum_SchedulingAuctionStatus.Scheduled`
      - Set **isRound2Active** = `AuctionUI.enum_SchedulingAuctionStatus.Scheduled`
      - Set **Round3_Status** = `AuctionUI.enum_SchedulingAuctionStatus.Scheduled`**
5. **Call Microflow **AuctionUI.ACT_LoadBuyerTotals****
6. 🏁 **END:** Return `$SchedulingAuction_Helper`

**Final Result:** This process concludes by returning a [Object] value.