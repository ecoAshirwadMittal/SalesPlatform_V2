# Microflow Detailed Specification: ACT_EntitySelect

### 📥 Inputs (Parameters)
- **$Entity** (Type: DeepLink.Entity)
- **$DeepLink** (Type: DeepLink.DeepLink)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$DeepLink**
      - Set **DeepLink_Entity** = `$Entity`
      - Set **ObjectType** = `$Entity/Name`**
2. **Close current page/popup**
3. **Maps to Page: **DeepLink.Attribute_Select****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.