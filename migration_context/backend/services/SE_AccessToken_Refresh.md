# Microflow Detailed Specification: SE_AccessToken_Refresh

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$ServiceType = DocumentGeneration.Enum_ServiceType._Cloud`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `false`
   ➔ **If [true]:**
      1. **Call Microflow **DocumentGeneration.DS_Configuration_FindOrCreate** (Result: **$Configuration**)**
      2. 🔀 **DECISION:** `$Configuration/RegistrationStatus = DocumentGeneration.Enum_RegistrationStatus.Registered`
         ➔ **If [true]:**
            1. **Create Variable **$ReferenceDate** = `addHours([%CurrentDateTime%], @DocumentGeneration.MaxHoursBeforeTokenRefresh)`**
            2. 🔀 **DECISION:** `$Configuration/AccessTokenExpirationDate = empty or $Configuration/AccessTokenExpirationDate < $ReferenceDate`
               ➔ **If [true]:**
                  1. **JavaCallAction**
                  2. 🏁 **END:** Return `$IsSuccess`
               ➔ **If [false]:**
                  1. **LogMessage**
                  2. 🏁 **END:** Return `false`
         ➔ **If [false]:**
            1. **LogMessage**
            2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.