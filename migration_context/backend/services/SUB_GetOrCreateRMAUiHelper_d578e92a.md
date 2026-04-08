# Microflow Analysis: SUB_GetOrCreateRMAUiHelper

### Requirements (Inputs):
- **$RMAMasterHelper** (A record of type: EcoATM_RMA.RMAMasterHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$RMAUiHelperList****
2. **Decision:** "Is List Empty?
"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Finish**
3. **Create Object
      - Store the result in a new variable called **$NewRMAUiHelper****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
