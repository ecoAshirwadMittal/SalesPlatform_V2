# Microflow Detailed Specification: MB_ChangePasswordSave

### 📥 Inputs (Parameters)
- **$PasswordData** (Type: Encryption.PasswordData)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$PasswordData/ConfirmPassword != empty and $PasswordData/ConfirmPassword != '' and $PasswordData/NewPassword != empty and $PasswordData/NewPassword != ''`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$PasswordData/NewPassword = $PasswordData/ConfirmPassword`
         ➔ **If [true]:**
            1. **Call Microflow **Encryption.Encrypt** (Result: **$EncryptedPassword**)**
            2. **Retrieve related **PasswordData_ExampleConfiguration** via Association from **$PasswordData** (Result: **$ExampleConfiguration**)**
            3. **Update **$ExampleConfiguration** (and Save to DB)
      - Set **Password** = `$EncryptedPassword`**
            4. **Delete**
            5. **Close current page/popup**
            6. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.