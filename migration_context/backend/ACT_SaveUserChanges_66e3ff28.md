# Microflow Analysis: ACT_SaveUserChanges

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "AuctionUI.VAL_EcoAtmUser"
      - Store the result in a new variable called **$IsValid****
5. **Decision:** "Valid?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
