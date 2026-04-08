# Microflow Detailed Specification: DS_GetKeyDescriptor

### 📥 Inputs (Parameters)
- **$KeyInfo** (Type: SAML20.KeyInfo)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **SAML20.KeyDescriptor** Filter: `[SAML20.KeyDescriptor_KeyInfo = $KeyInfo]` (Result: **$KeyDescriptorType**)**
2. 🏁 **END:** Return `$KeyDescriptorType`

**Final Result:** This process concludes by returning a [Object] value.