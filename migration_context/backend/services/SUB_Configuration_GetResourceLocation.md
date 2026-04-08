# Microflow Detailed Specification: SUB_Configuration_GetResourceLocation

### 📥 Inputs (Parameters)
- **$Configuration** (Type: Custom_Logging.Configuration)
- **$EnvironmentName** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Configuration/IsPerEnv`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$EnvironmentName+'\'+$Configuration/Filename`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$Configuration/Filename`

**Final Result:** This process concludes by returning a [String] value.