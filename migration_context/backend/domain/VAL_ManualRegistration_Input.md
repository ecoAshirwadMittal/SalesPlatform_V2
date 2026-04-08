# Microflow Detailed Specification: VAL_ManualRegistration_Input

### 📥 Inputs (Parameters)
- **$RegistrationWizard** (Type: DocumentGeneration.RegistrationWizard)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$isValid** = `true`**
2. 🔀 **DECISION:** `trim($RegistrationWizard/ApplicationUrl) != ''`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$RegistrationWizard/ManualAccessToken != empty and trim($RegistrationWizard/ManualAccessToken) != ''`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$RegistrationWizard/ManualRefreshToken != empty and trim($RegistrationWizard/ManualRefreshToken) != ''`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$RegistrationWizard/ManualRefreshToken != empty and trim($RegistrationWizard/ManualRefreshToken) != ''`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. **Update Variable **$isValid** = `false`**
      2. **ValidationFeedback**
      3. 🔀 **DECISION:** `$RegistrationWizard/ManualAccessToken != empty and trim($RegistrationWizard/ManualAccessToken) != ''`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$RegistrationWizard/ManualRefreshToken != empty and trim($RegistrationWizard/ManualRefreshToken) != ''`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$RegistrationWizard/ManualRefreshToken != empty and trim($RegistrationWizard/ManualRefreshToken) != ''`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.