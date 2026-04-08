# Microflow Detailed Specification: BCo_Sheet

### 📥 Inputs (Parameters)
- **$MxSheet** (Type: XLSReport.MxSheet)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxColumn** Filter: `[XLSReport.MxData_MxSheet = $MxSheet] [DataAggregate]` (Result: **$MxColumnList**)**
2. **AggregateList**
3. **Update **$MxSheet**
      - Set **FormLayout_GroupBy** = `$count > 0`**
4. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.