# Microflow Analysis: IVK_CreateDeeplink

### Requirements (Inputs):
- **$Configuration** (A record of type: ForgotPassword.Configuration)

### Execution Steps:
1. **Decision:** "already has deeplink?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
2. **Java Action Call
      - Store the result in a new variable called **$res****
3. **Search the Database for **DeepLink.Microflow** using filter: { [Name='ForgotPassword.Step3_DL_SetNewPassword'] } (Call this list **$Microflow**)**
4. **Create Object
      - Store the result in a new variable called **$NewDeepLink****
5. **Update the **$undefined** (Object):
      - Change [ForgotPassword.Configuration_DeepLink] to: "$NewDeepLink"
      - **Save:** This change will be saved to the database immediately.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
