# Microflow Detailed Specification: ACT_User_Create

### 📥 Inputs (Parameters)
- **$User** (Type: MicrosoftGraph.User)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Authorization_GetActive** (Result: **$Authorization**)**
2. **Call Microflow **MicrosoftGraph.SUB_User_Create** (Result: **$Created**)**
3. 🔀 **DECISION:** `$Created = true`
   ➔ **If [true]:**
      1. **Close current page/popup**
      2. **Update **$User****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `Creating User failed`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.