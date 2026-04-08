# Nanoflow: ACT_Authorization_Explore

**Allowed Roles:** MicrosoftGraph.Administrator

## 📥 Inputs

- **$Authentication** (MicrosoftGraph.Authentication)

## ⚙️ Execution Flow

1. **Call Microflow **MicrosoftGraph.SUB_Authorization_GetActiveByAuthentication** (Result: **$Authorization**)**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [true]:**
      1. **Open Page: **MicrosoftGraph.Authorization_Resources_View****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Open Page: **MicrosoftGraph.Authentication_NewEdit****
      2. **Show Message (Warning): `No successful authorization found. Please request authorization before accessing resources.`**
      3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
