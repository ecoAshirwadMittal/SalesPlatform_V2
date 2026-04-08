# Nanoflow: ACT_StringArrayWrapper_AddSelectedScope

**Allowed Roles:** MicrosoftGraph.Administrator

## 📥 Inputs

- **$Authentication** (MicrosoftGraph.Authentication)

## ⚙️ Execution Flow

1. **Retrieve related **Scopes** via Association from **$Authentication** (Result: **$StringArray**)**
2. **Create **MicrosoftGraph.StringArrayWrapper** (Result: **$NewStringArrayWrapper**)
      - Set **StringArrayWrapper_StringArray** = `$StringArray`**
3. **Update **$Authentication**
      - Set **SelectedScopes** = `$NewStringArrayWrapper`**
4. **Open Page: **MicrosoftGraph.StringArrayWrapper_NewEdit****
5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
