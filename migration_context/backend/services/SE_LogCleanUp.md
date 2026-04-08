# Microflow Detailed Specification: SE_LogCleanUp

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **SAML20.SPMetadata**  (Result: **$Configuration**)**
2. **Create Variable **$DeleteBeforeDate** = `addDays([%CurrentDateTime%], 1)`**
3. 🔀 **DECISION:** `$Configuration = empty`
   ➔ **If [false]:**
      1. **Update Variable **$DeleteBeforeDate** = `addDays([%CurrentDateTime%], -1 * $Configuration/LogAvailableDays)`**
      2. **DB Retrieve **SAML20.SAMLRequest** Filter: `[createdDate < $DeleteBeforeDate]` (Result: **$SAMLRequestList**)**
      3. 🔀 **DECISION:** `$SAMLRequestList != empty`
         ➔ **If [false]:**
            1. **DB Retrieve **SAML20.SSOLog** Filter: `[createdDate < $DeleteBeforeDate]` (Result: **$SSOLogList**)**
            2. 🔀 **DECISION:** `$SSOLogList != empty`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Delete**
                     *(Merging with existing path logic)*
         ➔ **If [true]:**
            1. **Delete**
               *(Merging with existing path logic)*
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.