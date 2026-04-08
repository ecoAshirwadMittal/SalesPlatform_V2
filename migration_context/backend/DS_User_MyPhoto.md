# Microflow Detailed Specification: DS_User_MyPhoto

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_User_GetMyPhoto** (Result: **$MyPhoto**)**
2. 🔀 **DECISION:** `$MyPhoto != empty`
   ➔ **If [true]:**
      1. **Update **$MyPhoto**
      - Set **ProfilePhoto_User** = `$currentUser`**
      2. 🏁 **END:** Return `$MyPhoto`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.