# Microflow Detailed Specification: SUB_Import_MxData

### 📥 Inputs (Parameters)
- **$MxSheet** (Type: XLSReport.MxSheet)
- **$NewMxTemplate** (Type: XLSReport.MxTemplate)
- **$NewMxSheet** (Type: XLSReport.MxSheet)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxData_MxSheet** via Association from **$MxSheet** (Result: **$MxDataList**)**
2. 🔄 **LOOP:** For each **$IteratorMxData** in **$MxDataList**
   └─ **End Loop**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.