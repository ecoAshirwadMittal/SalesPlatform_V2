# Nanoflow: ACT_Authentication_Save

**Allowed Roles:** MicrosoftGraph.Administrator

## 📥 Inputs

- **$Authentication** (MicrosoftGraph.Authentication)

## ⚙️ Execution Flow

1. **Call Microflow **MicrosoftGraph.Val_Authentication** (Result: **$Valid**)**
2. 🔀 **DECISION:** `$Valid`
   ➔ **If [true]:**
      1. **Commit/Save **$Authentication** to Database**
      2. **Close current page/popup**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
