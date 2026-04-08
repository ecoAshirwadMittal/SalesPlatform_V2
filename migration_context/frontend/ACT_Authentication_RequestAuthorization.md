# Nanoflow: ACT_Authentication_RequestAuthorization

**Allowed Roles:** MicrosoftGraph.Administrator, MicrosoftGraph.User

## 📥 Inputs

- **$Authentication** (MicrosoftGraph.Authentication)

## ⚙️ Execution Flow

1. **Call Microflow **MicrosoftGraph.Val_Authentication** (Result: **$Valid**)**
2. 🔀 **DECISION:** `$Valid`
   ➔ **If [true]:**
      1. **Commit/Save **$Authentication** to Database**
      2. **Call Microflow **MicrosoftGraph.SUB_Authentication_GetUserAccessURL** (Result: **$url**)**
      3. 🔀 **DECISION:** `$url != empty`
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
