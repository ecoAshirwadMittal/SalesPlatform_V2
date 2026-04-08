# Microflow Analysis: ACT_Create_Auction

### Requirements (Inputs):
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Week****
2. **Create Object
      - Store the result in a new variable called **$NewAuction****
3. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggInventoryHelper.HasAuctionBeenTriggered] to: "true
"
      - Change [AuctionUI.AggInventoryHelper_Auction] to: "$NewAuction
"
      - Change [AuctionUI.AggInventoryHelper.WasTitleEmpty] to: "trim($SchedulingAuction_Helper/Auction_Name)= ''
"
      - Change [AuctionUI.AggInventoryHelper.HasAuction] to: "true
"
      - Change [AuctionUI.AggInventoryHelper.HasSchedule] to: "false
"**
4. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction_Helper.Auction_Name] to: "if trim($AggInventoryHelper/AuctionName) != ''
then
'Auction ' + $Week/WeekDisplay + ' ' + $AggInventoryHelper/AuctionName
else
'Auction ' + $Week/WeekDisplay 
"**
5. **Update the **$undefined** (Object):
      - Change [AuctionUI.Auction.AuctionTitle] to: "$SchedulingAuction_Helper/Auction_Name
"**
6. **Create Variable**
7. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction_Helper.Round1_Start_DateTime] to: "addHours($Week/WeekStartDateTime,$StartHourOffset)"
      - Change [AuctionUI.SchedulingAuction_Helper.Round1_End_DateTime] to: "addHours($Week/WeekStartDateTime,$StartHourOffset+87)"
      - Change [AuctionUI.SchedulingAuction_Helper.Round2_Start_DateTime] to: "addHours($Week/WeekStartDateTime,$StartHourOffset+88)"
      - Change [AuctionUI.SchedulingAuction_Helper.Round2_End_DateTime] to: "addHours($Week/WeekStartDateTime,$StartHourOffset+112)"
      - Change [AuctionUI.SchedulingAuction_Helper.Round3_Start_DateTime] to: "addHours($Week/WeekStartDateTime,$StartHourOffset+113)"
      - Change [AuctionUI.SchedulingAuction_Helper.Round3_End_Datetime] to: "addHours($Week/WeekStartDateTime,$StartHourOffset+140)"
      - Change [AuctionUI.SchedulingAuction_Helper_Auction] to: "$Week/AuctionUI.Auction_Week/AuctionUI.Auction"
      - Change [AuctionUI.SchedulingAuction_Helper.Round1_Status] to: "AuctionUI.enum_SchedulingAuctionStatus.Scheduled"
      - Change [AuctionUI.SchedulingAuction_Helper.isRound2Active] to: "AuctionUI.enum_SchedulingAuctionStatus.Scheduled"
      - Change [AuctionUI.SchedulingAuction_Helper.Round3_Status] to: "AuctionUI.enum_SchedulingAuctionStatus.Scheduled"
      - Change [AuctionUI.SchedulingAuction_Helper.Auction_Week_Year] to: "$Week/WeekDisplay
"**
8. **Run another process: "AuctionUI.ACT_Create_SchedulingAuction_Helper_Default"
      - Store the result in a new variable called **$SchedulingAuction_Helper_2****
9. **Run another process: "AuctionUI.ACT_LoadBuyerTotals"**
10. **Show Page**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
