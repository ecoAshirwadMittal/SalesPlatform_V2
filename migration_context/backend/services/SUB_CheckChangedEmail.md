# Microflow Detailed Specification: SUB_CheckChangedEmail

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[ ( SubmissionID = $EcoATMDirectUser/SubmissionID ) ]` (Result: **$ExistingEcoATMDirectUser**)**
2. 🔀 **DECISION:** `$EcoATMDirectUser/Email != $ExistingEcoATMDirectUser/Email`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.