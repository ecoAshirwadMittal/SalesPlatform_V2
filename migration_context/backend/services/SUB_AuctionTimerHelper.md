# Microflow Detailed Specification: SUB_AuctionTimerHelper

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **AuctionUI.ACT_GetTimeOffset** (Result: **$TimeZoneOffset**)**
3. **Create Variable **$CurrentTime** = `subtractHours([%CurrentDateTime%],$TimeZoneOffset)`**
4. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $Week]` (Result: **$Auction**)**
5. **List Operation: **Find** on **$undefined** where `1` (Result: **$NewSchedulingAuction1**)**
6. **List Operation: **Find** on **$undefined** where `2` (Result: **$NewSchedulingAuction2**)**
7. **List Operation: **Find** on **$undefined** where `3` (Result: **$NewSchedulingAuction3**)**
8. **Create Variable **$Hot1** = `if $NewSchedulingAuction1/Start_DateTime != empty then 'Ends in | ' + toString( floor( daysBetween( $NewSchedulingAuction1/End_DateTime, $CurrentTime) ) ) + 'D. | ' + toString( floor( hoursBetween( $NewSchedulingAuction1/End_DateTime, $CurrentTime) mod 24 ) ) + 'hrs. | ' + toString( floor( minutesBetween( $NewSchedulingAuction1/End_DateTime, $CurrentTime) mod 60 ) ) + 'min. |' else if $NewSchedulingAuction1/End_DateTime < $CurrentTime then 'Ended on ' + toString(parseDateTime(formatDateTimeUTC($NewSchedulingAuction1/End_DateTime, 'yyyy-MM-dd HH:mm:ss'), 'yyyy-MM-dd HH:mm:ss')) + ' EST' else ''`**
9. **Create Variable **$Hot2** = `if $NewSchedulingAuction2/Start_DateTime != empty then 'Ends in | ' + toString( floor( daysBetween( $NewSchedulingAuction2/End_DateTime, $CurrentTime) ) ) + 'D. | ' + toString( floor( hoursBetween( $NewSchedulingAuction2/End_DateTime, $CurrentTime) mod 24 ) ) + 'hrs. | ' + toString( floor( minutesBetween( $NewSchedulingAuction2/End_DateTime, $CurrentTime) mod 60 ) ) + 'min. |' else ''`**
10. **Create Variable **$Hot3** = `if $NewSchedulingAuction3/Start_DateTime != empty then 'Ends in | ' + toString( floor( daysBetween( $NewSchedulingAuction3/End_DateTime, $CurrentTime) ) ) + 'D. | ' + toString( floor( hoursBetween( $NewSchedulingAuction3/End_DateTime, $CurrentTime) mod 24 ) ) + 'hrs. | ' + toString( floor( minutesBetween( $NewSchedulingAuction3/End_DateTime, $CurrentTime) mod 60 ) ) + 'min. |' else ''`**
11. **Create **AuctionUI.AuctionTimerHelper** (Result: **$NewAuctionTimerHelper**)
      - Set **Hot1Text** = `$Hot1`
      - Set **Hot2Text** = `$Hot2`
      - Set **Hot3Text** = `$Hot3`**
12. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
13. 🏁 **END:** Return `$NewAuctionTimerHelper`

**Final Result:** This process concludes by returning a [Object] value.