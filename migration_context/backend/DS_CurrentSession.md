# Microflow Detailed Specification: DS_CurrentSession

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **System.Session** Filter: `[id = $currentSession]` (Result: **$Session**)**
2. 🏁 **END:** Return `$Session`

**Final Result:** This process concludes by returning a [Object] value.