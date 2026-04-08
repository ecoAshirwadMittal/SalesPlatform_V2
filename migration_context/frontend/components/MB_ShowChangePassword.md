# Microflow Detailed Specification: MB_ShowChangePassword

### 📥 Inputs (Parameters)
- **$ExampleConfiguration** (Type: Encryption.ExampleConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **Encryption.PasswordData** (Result: **$NewPasswordData**)
      - Set **PasswordData_ExampleConfiguration** = `$ExampleConfiguration`**
2. **Maps to Page: **Encryption.ChangePassword****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.