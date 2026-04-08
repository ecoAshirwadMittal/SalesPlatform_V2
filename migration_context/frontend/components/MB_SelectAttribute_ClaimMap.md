# Microflow Detailed Specification: MB_SelectAttribute_ClaimMap

### 📥 Inputs (Parameters)
- **$Attribute** (Type: SAML20.Attribute)
- **$ClaimMap** (Type: SAML20.ClaimMap)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$ClaimMap**
      - Set **ClaimMap_Attribute** = `$Attribute`**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.