# Microflow Detailed Specification: IVK_DeleteColumn

### 📥 Inputs (Parameters)
- **$MxColumn** (Type: XLSReport.MxColumn)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxColumn** Filter: `[XLSReport.MxData_MxSheet = $MxColumn/XLSReport.MxData_MxSheet] [ColumnNumber > $MxColumn/ColumnNumber]` (Result: **$MxColumnList**)**
2. 🔄 **LOOP:** For each **$HigherMxColumn** in **$MxColumnList**
   │ 1. **Update **$HigherMxColumn**
      - Set **ColumnNumber** = `$HigherMxColumn/ColumnNumber - 1`**
   └─ **End Loop**
3. **Commit/Save **$MxColumnList** to Database**
4. **Delete**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.