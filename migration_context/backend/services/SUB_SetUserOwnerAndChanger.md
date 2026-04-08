# Microflow Detailed Specification: SUB_SetUserOwnerAndChanger

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `isNew($EcoATMDirectUser)`
   ➔ **If [true]:**
      1. **Update **$EcoATMDirectUser**
      - Set **EntityOwner** = `$currentUser/Name`
      - Set **EntityChanger** = `$currentUser/Name`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **DB Retrieve **System.User** Filter: `[id=$EcoATMDirectUser/System.owner]` (Result: **$User**)**
      2. **Update **$EcoATMDirectUser**
      - Set **EntityOwner** = `if $User!=empty then $User/Name else $currentUser/Name`
      - Set **EntityChanger** = `$currentUser/Name`**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.