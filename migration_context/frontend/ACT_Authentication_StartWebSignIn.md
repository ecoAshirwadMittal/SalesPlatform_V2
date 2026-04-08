# Nanoflow: ACT_Authentication_StartWebSignIn

**Allowed Roles:** MicrosoftGraph.Administrator, MicrosoftGraph.User

## ⚙️ Execution Flow

1. **DB Retrieve **MicrosoftGraph.Authentication** Filter: `[IsActive]` (Result: **$Authentication**)**
2. 🔀 **DECISION:** `$Authentication != empty`
   ➔ **If [true]:**
      1. **Call Microflow **MicrosoftGraph.SUB_Authentication_GetUserAccessURL** (Result: **$URL**)**
      2. 🔀 **DECISION:** `$URL != empty`
         ➔ **If [true]:**
            1. **Call JS Action **NanoflowCommons.OpenURL** (Result: **$ReturnValueName**)**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Show Message (Information): `This functionality is currently unavailable. Please contact an administrator.`**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `This functionality is currently unavailable. Please contact an administrator.`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
