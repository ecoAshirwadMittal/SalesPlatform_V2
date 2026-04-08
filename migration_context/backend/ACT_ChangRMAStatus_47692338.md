# Microflow Analysis: ACT_ChangRMAStatus

### Requirements (Inputs):
- **$RMAUiHelper** (A record of type: EcoATM_RMA.RMAUiHelper)
- **$RMAMasterHelper** (A record of type: EcoATM_RMA.RMAMasterHelper)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMAUiHelper_RMAMasterHelper_Selected] to: "$RMAUiHelper"
      - **Save:** This change will be saved to the database immediately.**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
