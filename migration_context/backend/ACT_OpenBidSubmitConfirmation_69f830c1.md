# Microflow Analysis: ACT_OpenBidSubmitConfirmation

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$AuctionTimerHelper** (A record of type: AuctionUI.AuctionTimerHelper)
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Log Message**
2. **Retrieve
      - Store the result in a new variable called **$BidRound****
3. **Run another process: "AuctionUI.ACT_BidSubmit_check_for_bids"
      - Store the result in a new variable called **$HasBids****
4. **Decision:** "Has Bids ?"
   - If [true] -> Move to: **exits?**
   - If [false] -> Move to: **Activity**
5. **Decision:** "exits?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
7. **Decision:** "Is this upsell round?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
8. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper.isUpsellRound] to: "true"**
9. **Show Page**
10. **Log Message**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
