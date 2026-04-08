# Microflow Detailed Specification: SUB_ConfigurationByFIle_GetOrCreate

### 📥 Inputs (Parameters)
- **$ResourceFilename** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Custom_Logging.ConfigurationByFile** Filter: `[FileName=$ResourceFilename]` (Result: **$ConfigurationByFile**)**
2. 🔀 **DECISION:** `$ConfigurationByFile!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$ConfigurationByFile`
   ➔ **If [false]:**
      1. **Create **Custom_Logging.ConfigurationByFile** (Result: **$NewConfigurationByFile**)
      - Set **FileName** = `$ResourceFilename`
      - Set **MD5** = `'NEW_FILE'`**
      2. 🏁 **END:** Return `$NewConfigurationByFile`

**Final Result:** This process concludes by returning a [Object] value.