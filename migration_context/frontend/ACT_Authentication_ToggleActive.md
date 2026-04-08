# Nanoflow: ACT_Authentication_ToggleActive

**Allowed Roles:** MicrosoftGraph.Administrator

## 📥 Inputs

- **$Authentication** (MicrosoftGraph.Authentication)

## ⚙️ Execution Flow

1. **Update **$Authentication**
      - Set **IsActive** = `true`**
2. **DB Retrieve **MicrosoftGraph.Authentication** Filter: `[IsActive]` (Result: **$AuthenticationList**)**
3. 🔄 **LOOP:** For each **$IteratorAuthenticationList** in **$AuthenticationList**
   │ 1. **Update **$IteratorAuthenticationList**
      - Set **IsActive** = `false`**
   └─ **End Loop**
4. **Commit/Save **$AuthenticationList** to Database**
5. **Commit/Save **$Authentication** to Database**
6. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
