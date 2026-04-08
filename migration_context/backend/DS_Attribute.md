# Microflow Detailed Specification: DS_Attribute

### 📥 Inputs (Parameters)
- **$DeepLink** (Type: DeepLink.DeepLink)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **DeepLink.Entity** Filter: `[Name = $DeepLink/ObjectType]` (Result: **$Entity**)**
2. **Retrieve related **Attribute_Entity** via Association from **$Entity** (Result: **$AttributeList**)**
3. 🏁 **END:** Return `$AttributeList`

**Final Result:** This process concludes by returning a [List] value.