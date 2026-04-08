# Microflow Detailed Specification: ACT_ToggleUserActive

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$newActiveValue** = `true`**
2. 🔀 **DECISION:** `$EcoATMDirectUser/Active = true`
   ➔ **If [true]:**
      1. **Update Variable **$newActiveValue** = `false`**
      2. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Active** = `$newActiveValue`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Active** = `$newActiveValue`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.