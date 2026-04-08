# Microflow Detailed Specification: SUB_Authorization_GetOrCreate

### 📥 Inputs (Parameters)
- **$Authentication** (Type: MicrosoftGraph.Authentication)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MicrosoftGraph.Authorization** Filter: `[MicrosoftGraph.Authorization_Authentication = $Authentication] [MicrosoftGraph.Authorization_User = $currentUser]` (Result: **$Authorization**)**
2. **JavaCallAction**
3. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [true]:**
      1. **Update **$Authorization**
      - Set **State** = `$State`**
      2. 🏁 **END:** Return `$Authorization`
   ➔ **If [false]:**
      1. **Create **MicrosoftGraph.Authorization** (Result: **$NewAuthorization**)
      - Set **Authorization_User** = `$currentUser`
      - Set **Authorization_Authentication** = `$Authentication`
      - Set **State** = `$State`**
      2. 🏁 **END:** Return `$NewAuthorization`

**Final Result:** This process concludes by returning a [Object] value.