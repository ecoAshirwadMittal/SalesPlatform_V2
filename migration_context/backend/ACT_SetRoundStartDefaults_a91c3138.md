# Microflow Analysis: ACT_SetRoundStartDefaults

### Requirements (Inputs):
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction_Helper.Round2_Start_DateTime] to: "addMinutes($SchedulingAuction_Helper/Round1_End_DateTime,$BuyerCodeSubmitConfig/AuctionRound2MinutesOffset)
"
      - Change [AuctionUI.SchedulingAuction_Helper.Round3_Start_DateTime] to: "addMinutes($SchedulingAuction_Helper/Round2_End_DateTime,$BuyerCodeSubmitConfig/AuctionRound3MinutesOffset)
"
      - **Save:** This change will be saved to the database immediately.**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
