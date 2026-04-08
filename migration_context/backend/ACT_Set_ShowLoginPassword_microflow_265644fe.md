# Microflow Analysis: ACT_Set_ShowLoginPassword_microflow

### Requirements (Inputs):
- **$LoginCredentials** (A record of type: AuctionUI.LoginCredentials)

### Execution Steps:
1. **Decision:** "Is EcoAtm Email?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.LoginCredentials.ShowPasssword] to: "true"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
