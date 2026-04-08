# Microflow Detailed Specification: VAL_PasswordRequirements

### 📥 Inputs (Parameters)
- **$AccountPasswordData** (Type: ForgotPassword.AccountPasswordData)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$AccountPasswordData/NewPassword!=empty`
   ➔ **If [true]:**
      1. **Update **$AccountPasswordData**
      - Set **IsLengthValid** = `if length($AccountPasswordData/NewPassword) >= 8 then AuctionUI.ENUM_PasswordStatus.Valid else AuctionUI.ENUM_PasswordStatus.Invalid`
      - Set **HasUppercaseLetter** = `if isMatch($AccountPasswordData/NewPassword, '.*[A-Z].*') then AuctionUI.ENUM_PasswordStatus.Valid else AuctionUI.ENUM_PasswordStatus.Invalid`
      - Set **HasSpecialCharacter** = `if isMatch($AccountPasswordData/NewPassword, '.*[^a-zA-Z0-9].*') then AuctionUI.ENUM_PasswordStatus.Valid else AuctionUI.ENUM_PasswordStatus.Invalid`**
      2. 🏁 **END:** Return `$AccountPasswordData/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/HasUppercaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid`
   ➔ **If [false]:**
      1. **Update **$AccountPasswordData**
      - Set **IsLengthValid** = `AuctionUI.ENUM_PasswordStatus.Neutral`
      - Set **HasUppercaseLetter** = `AuctionUI.ENUM_PasswordStatus.Neutral`
      - Set **HasSpecialCharacter** = `AuctionUI.ENUM_PasswordStatus.Neutral`**
      2. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.