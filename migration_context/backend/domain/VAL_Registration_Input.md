# Microflow Detailed Specification: VAL_Registration_Input

### 📥 Inputs (Parameters)
- **$RegistrationWizard** (Type: DocumentGeneration.RegistrationWizard)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$isValid** = `true`**
2. 🔀 **DECISION:** `$RegistrationWizard/DeploymentType != DocumentGeneration.Enum_DeploymentType.Other`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `trim($RegistrationWizard/PersonalAccessToken) != ''`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `trim($RegistrationWizard/ApplicationUrl) != ''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `trim($RegistrationWizard/AppId) != ''`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `trim($RegistrationWizard/AppId) != ''`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `trim($RegistrationWizard/ApplicationUrl) != ''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `trim($RegistrationWizard/AppId) != ''`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `trim($RegistrationWizard/AppId) != ''`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. **Update Variable **$isValid** = `false`**
      2. **ValidationFeedback**
      3. 🔀 **DECISION:** `trim($RegistrationWizard/PersonalAccessToken) != ''`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `trim($RegistrationWizard/ApplicationUrl) != ''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `trim($RegistrationWizard/AppId) != ''`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `trim($RegistrationWizard/AppId) != ''`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `trim($RegistrationWizard/ApplicationUrl) != ''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `trim($RegistrationWizard/AppId) != ''`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `trim($RegistrationWizard/AppId) != ''`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.