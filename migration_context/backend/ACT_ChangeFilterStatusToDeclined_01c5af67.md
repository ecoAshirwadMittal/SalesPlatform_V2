# Microflow Analysis: ACT_ChangeFilterStatusToDeclined

### Requirements (Inputs):
- **$RMAFilterHelper** (A record of type: EcoATM_RMA.RMAFilterHelper)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMAFilterHelper.FilterStatus] to: "EcoATM_RMA.ENUM_RMAItemStatus.Decline"**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
