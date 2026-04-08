# Microflow Analysis: SUB_ProcessFailureCleanup

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)
- **$RMARequest_ImportHelperList** (A record of type: EcoATM_RMA.RMARequest_ImportHelper)

### Execution Steps:
1. **Run another process: "EcoATM_RMA.SUB_DeleteUploadHelpers"**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
