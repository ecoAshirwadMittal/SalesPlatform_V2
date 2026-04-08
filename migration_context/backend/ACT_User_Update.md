# Microflow Detailed Specification: ACT_User_Update

### 📥 Inputs (Parameters)
- **$User** (Type: MicrosoftGraph.User)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MicrosoftGraph.Authorization** Filter: `[MicrosoftGraph.Authorization_User = $currentUser]` (Result: **$Authorization**)**
2. **Call Microflow **MicrosoftGraph.SUB_User_Update** (Result: **$Updated**)**
3. 🔀 **DECISION:** `$Updated = true`
   ➔ **If [true]:**
      1. **Close current page/popup**
      2. **Update **$User****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `Update failed`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.