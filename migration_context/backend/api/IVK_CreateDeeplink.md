# Microflow Detailed Specification: IVK_CreateDeeplink

### 📥 Inputs (Parameters)
- **$Configuration** (Type: ForgotPassword.Configuration)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Configuration/ForgotPassword.Configuration_DeepLink != empty`
   ➔ **If [false]:**
      1. **JavaCallAction**
      2. **DB Retrieve **DeepLink.Microflow** Filter: `[Name='ForgotPassword.Step3_DL_SetNewPassword']` (Result: **$Microflow**)**
      3. **Create **DeepLink.DeepLink** (Result: **$NewDeepLink**)
      - Set **Name** = `'ForgotPassword'`
      - Set **Microflow** = `$Microflow/Name`
      - Set **AllowGuests** = `true`
      - Set **UseStringArgument** = `true`
      - Set **SeparateGetParameters** = `true`**
      4. **Update **$Configuration** (and Save to DB)
      - Set **Configuration_DeepLink** = `$NewDeepLink`**
      5. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **Configuration_DeepLink** via Association from **$Configuration** (Result: **$DeepLink**)**
      2. **Delete**
      3. **JavaCallAction**
      4. **DB Retrieve **DeepLink.Microflow** Filter: `[Name='ForgotPassword.Step3_DL_SetNewPassword']` (Result: **$Microflow**)**
      5. **Create **DeepLink.DeepLink** (Result: **$NewDeepLink**)
      - Set **Name** = `'ForgotPassword'`
      - Set **Microflow** = `$Microflow/Name`
      - Set **AllowGuests** = `true`
      - Set **UseStringArgument** = `true`
      - Set **SeparateGetParameters** = `true`**
      6. **Update **$Configuration** (and Save to DB)
      - Set **Configuration_DeepLink** = `$NewDeepLink`**
      7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.