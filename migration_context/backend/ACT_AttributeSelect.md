# Microflow Detailed Specification: ACT_AttributeSelect

### 📥 Inputs (Parameters)
- **$Attribute** (Type: DeepLink.Attribute)
- **$DeepLink** (Type: DeepLink.DeepLink)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$DeepLink**
      - Set **DeepLink_Attribute** = `$Attribute`
      - Set **ObjectAttribute** = `$Attribute/Name`**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.