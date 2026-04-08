# Microflow Analysis: ACT_CreateActivateUserURL

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "ForgotPassword.GetValidUntilDateTime"
      - Store the result in a new variable called **$ValidUntil****
3. **Create Object
      - Store the result in a new variable called **$NewForgotPassword****
4. **Run another process: "AuctionUI.GenerateActivationUIDAndURL"
      - Store the result in a new variable called **$URL****
5. **Run another process: "AuctionUI.ACT_UpdateActivationDate"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
