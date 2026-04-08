# Microflow Detailed Specification: BCo_MxData

### 📥 Inputs (Parameters)
- **$MxData** (Type: XLSReport.MxData)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxData_MxSheet** via Association from **$MxData** (Result: **$MxSheet**)**
2. **Create Variable **$ColumnIndex** = `-1`**

**Final Result:** This process concludes by returning a [Boolean] value.