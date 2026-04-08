# Microflow Analysis: ACT_SoftDelete_BuyerCode

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.BuyerCode.softDelete] to: "true
"**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
