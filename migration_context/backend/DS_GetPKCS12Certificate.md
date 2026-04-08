# Microflow Detailed Specification: DS_GetPKCS12Certificate

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Pk12Certificate_EmailAccount** via Association from **$EmailAccount** (Result: **$Pk12Certificate**)**

**Final Result:** This process concludes by returning a [Object] value.