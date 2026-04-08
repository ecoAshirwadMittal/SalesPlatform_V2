# Nanoflow: ACT_Authentication_SetDefault

**Allowed Roles:** MicrosoftGraph.Administrator

## 📥 Inputs

- **$Authentication** (MicrosoftGraph.Authentication)

## ⚙️ Execution Flow

1. **DB Retrieve **MicrosoftGraph.Authentication** Filter: `[id != $Authentication/id] [IsDefault]` (Result: **$AuthenticationList**)**
2. 🔄 **LOOP:** For each **$IteratorAuthenticationList** in **$AuthenticationList**
   │ 1. **Update **$IteratorAuthenticationList**
      - Set **IsDefault** = `false`**
   └─ **End Loop**
3. **Commit/Save **$AuthenticationList** to Database**
4. **Update **$Authentication** (and Save to DB)
      - Set **IsDefault** = `true`
      - Set **IsActive** = `true`**
5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
