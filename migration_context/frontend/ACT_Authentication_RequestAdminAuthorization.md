# Nanoflow: ACT_Authentication_RequestAdminAuthorization

**Allowed Roles:** MicrosoftGraph.Administrator

## 📥 Inputs

- **$Authentication** (MicrosoftGraph.Authentication)

## ⚙️ Execution Flow

1. **Call Microflow **MicrosoftGraph.Val_Authentication** (Result: **$Valid**)**
2. 🔀 **DECISION:** `$Valid`
   ➔ **If [true]:**
      1. **Call Microflow **MicrosoftGraph.SUB_Authentication_GetAdminAccessURL** (Result: **$URL**)**
      2. **Call JS Action **NanoflowCommons.OpenURL** (Result: **$ReturnValueName**)**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
