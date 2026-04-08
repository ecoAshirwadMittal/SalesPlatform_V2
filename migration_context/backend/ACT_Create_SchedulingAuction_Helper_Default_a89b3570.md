# Microflow Analysis: ACT_Create_SchedulingAuction_Helper_Default

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_GetTimeOffset"
      - Store the result in a new variable called **$TimezoneOffset****
2. **Create Variable**
3. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
4. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction_Helper.Round1_Start_DateTime] to: "addHours($Week/WeekStartDateTime,$StartHourOffset)
"
      - Change [AuctionUI.SchedulingAuction_Helper.Round1_End_DateTime] to: "addHours($Week/WeekStartDateTime,$StartHourOffset+87)
"
      - Change [AuctionUI.SchedulingAuction_Helper.Round2_Start_DateTime] to: "addMinutes($Week/WeekStartDateTime,60*($StartHourOffset+87)+ $BuyerCodeSubmitConfig/AuctionRound2MinutesOffset)
"
      - Change [AuctionUI.SchedulingAuction_Helper.Round2_End_DateTime] to: "addHours($Week/WeekStartDateTime,$StartHourOffset+112)
"
      - Change [AuctionUI.SchedulingAuction_Helper.Round3_Start_DateTime] to: "addMinutes($Week/WeekStartDateTime,60*($StartHourOffset+112)+$BuyerCodeSubmitConfig/AuctionRound3MinutesOffset)
"
      - Change [AuctionUI.SchedulingAuction_Helper.Round3_End_Datetime] to: "addHours($Week/WeekStartDateTime,$StartHourOffset+140)
"
      - Change [AuctionUI.SchedulingAuction_Helper_Auction] to: "$Week/AuctionUI.Auction_Week/AuctionUI.Auction"
      - Change [AuctionUI.SchedulingAuction_Helper.Round1_Status] to: "AuctionUI.enum_SchedulingAuctionStatus.Scheduled"
      - Change [AuctionUI.SchedulingAuction_Helper.isRound2Active] to: "AuctionUI.enum_SchedulingAuctionStatus.Scheduled"
      - Change [AuctionUI.SchedulingAuction_Helper.Round3_Status] to: "AuctionUI.enum_SchedulingAuctionStatus.Scheduled"**
5. **Run another process: "AuctionUI.ACT_LoadBuyerTotals"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
