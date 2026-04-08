# Microflow Detailed Specification: DS_GetCurrentIdToken

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MendixSSO.SUB_GetDecryptedTokenByTypeForCurrentSession** (Result: **$DecryptedToken**)**
2. 🏁 **END:** Return `$DecryptedToken`

**Final Result:** This process concludes by returning a [Object] value.