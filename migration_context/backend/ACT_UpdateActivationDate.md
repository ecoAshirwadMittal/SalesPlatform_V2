# Microflow Detailed Specification: ACT_UpdateActivationDate

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EcoATMDirectUser/InvitedDate = empty`
   ➔ **If [true]:**
      1. **Update **$EcoATMDirectUser**
      - Set **InvitedDate** = `[%CurrentDateTime%]`**
      2. **Update **$EcoATMDirectUser**
      - Set **LastInviteSent** = `[%CurrentDateTime%]`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$EcoATMDirectUser**
      - Set **LastInviteSent** = `[%CurrentDateTime%]`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.