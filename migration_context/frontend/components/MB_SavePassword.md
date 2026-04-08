# Microflow Detailed Specification: MB_SavePassword

### 📥 Inputs (Parameters)
- **$ExampleConfiguration** (Type: Encryption.ExampleConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Encryption.Encrypt** (Result: **$Encrypted**)**
2. **Update **$ExampleConfiguration** (and Save to DB)
      - Set **Password** = `$Encrypted`**
3. **Close current page/popup**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.