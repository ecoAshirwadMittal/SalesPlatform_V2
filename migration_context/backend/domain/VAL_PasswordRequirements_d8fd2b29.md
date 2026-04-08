# Microflow Analysis: VAL_PasswordRequirements

### Requirements (Inputs):
- **$AccountPasswordData** (A record of type: ForgotPassword.AccountPasswordData)

### Execution Steps:
1. **Decision:** "new password not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [ForgotPassword.AccountPasswordData.IsLengthValid] to: "if length($AccountPasswordData/NewPassword) >= 8
then
AuctionUI.ENUM_PasswordStatus.Valid
else
AuctionUI.ENUM_PasswordStatus.Invalid"
      - Change [ForgotPassword.AccountPasswordData.HasUppercaseLetter] to: "if isMatch($AccountPasswordData/NewPassword, '.*[A-Z].*')
then
 AuctionUI.ENUM_PasswordStatus.Valid
else
AuctionUI.ENUM_PasswordStatus.Invalid"
      - Change [ForgotPassword.AccountPasswordData.HasSpecialCharacter] to: "if isMatch($AccountPasswordData/NewPassword, '.*[^a-zA-Z0-9].*')
then AuctionUI.ENUM_PasswordStatus.Valid
else
AuctionUI.ENUM_PasswordStatus.Invalid"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
