# Microflow Detailed Specification: SUB_Authorization_SetNonce

### 📥 Inputs (Parameters)
- **$Scope** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `contains($Scope, 'id_token')`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. **Create Variable **$Nonce** = `'&nonce='+$RandomHash`**
      3. 🏁 **END:** Return `$Nonce`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `''`

**Final Result:** This process concludes by returning a [String] value.