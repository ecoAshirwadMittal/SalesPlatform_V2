# Microflow Detailed Specification: DS_RunAsUser

### 📥 Inputs (Parameters)
- **$UserName** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **System.User** Filter: `[Name=$UserName]` (Result: **$User**)**
2. 🔀 **DECISION:** `$User!=empty`
   ➔ **If [false]:**
      1. **JavaCallAction**
      2. **Create Variable **$RunAsUserRole** = `@TaskQueueScheduler.RunAsUserRole`**
      3. **DB Retrieve **System.UserRole** Filter: `[Name=$RunAsUserRole]` (Result: **$UserRoleList**)**
      4. **Create **System.User** (Result: **$NewUser**)
      - Set **Name** = `$UserName`
      - Set **Password** = `$Password`
      - Set **UserRoles** = `$UserRoleList`**
      5. 🏁 **END:** Return `$NewUser`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$User`

**Final Result:** This process concludes by returning a [Object] value.