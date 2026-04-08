# Microflow Detailed Specification: MB_DeleteAttribute

### 📥 Inputs (Parameters)
- **$Attribute** (Type: SAML20.Attribute)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Attribute/ManuallyCreated`
   ➔ **If [true]:**
      1. **Delete**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `This attribute can not be removed, only manually created attributes can be removed.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.