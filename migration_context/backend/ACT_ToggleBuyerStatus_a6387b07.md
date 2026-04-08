# Microflow Analysis: ACT_ToggleBuyerStatus

### Requirements (Inputs):
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Decision:** "Active?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.Status] to: "AuctionUI.enum_BuyerStatus.Disabled"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
