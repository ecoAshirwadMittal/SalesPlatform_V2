# Microflow Detailed Specification: DS_JWT

### 📥 Inputs (Parameters)
- **$ConnectionDetails** (Type: SnowflakeRESTSQL.ConnectionDetails)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **SnowflakeRESTSQL.JWT_GetCreate** (Result: **$JWT**)**
2. 🏁 **END:** Return `$JWT`

**Final Result:** This process concludes by returning a [Object] value.