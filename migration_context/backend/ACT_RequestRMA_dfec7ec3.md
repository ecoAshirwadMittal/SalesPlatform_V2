# Microflow Analysis: ACT_RequestRMA

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$IsClosePage** (A record of type: Object)

### Execution Steps:
1. **Run another process: "EcoATM_UserManagement.DS_GetCurrentEcoATMDirectUser"
      - Store the result in a new variable called **$EcoATMDirectUser****
2. **Create Object
      - Store the result in a new variable called **$NewRMA****
3. **Decision:** "Close page?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Close Form**
5. **Show Page**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
