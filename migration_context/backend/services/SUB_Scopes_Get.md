# Microflow Detailed Specification: SUB_Scopes_Get

### 📥 Inputs (Parameters)
- **$Authentication** (Type: MicrosoftGraph.Authentication)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SelectedScopes** via Association from **$Authentication** (Result: **$ScopeWrapperList**)**
2. **Create Variable **$Scope** = `empty`**
3. 🔄 **LOOP:** For each **$IteratorScopeWrapper** in **$ScopeWrapperList**
   │ 1. **Update Variable **$Scope** = `if $Scope = empty then $IteratorScopeWrapper/Value else $Scope + ' ' + $IteratorScopeWrapper/Value`**
   └─ **End Loop**
4. 🏁 **END:** Return `urlEncode($Scope)`

**Final Result:** This process concludes by returning a [String] value.