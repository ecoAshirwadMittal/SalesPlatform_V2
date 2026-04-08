# Microflow Detailed Specification: SUB_Import_MxColumn

### 📥 Inputs (Parameters)
- **$NewMxSheet** (Type: XLSReport.MxSheet)
- **$MxColumn** (Type: XLSReport.MxColumn)
- **$NewMxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **XLSReport.StringToMxCellStyle** (Result: **$MxCellStyle**)**
2. **Create **XLSReport.MxColumn** (Result: **$NewMxColumn**)
      - Set **ColumnNumber** = `$MxColumn/ColumnNumber`
      - Set **ObjectAttribute** = `$MxColumn/ObjectAttribute`
      - Set **DataAggregate** = `$MxColumn/DataAggregate`
      - Set **DataAggregateFunction** = `$MxColumn/DataAggregateFunction`
      - Set **ResultAggregate** = `$MxColumn/ResultAggregate`
      - Set **ResultAggregateFunction** = `$MxColumn/ResultAggregateFunction`
      - Set **Name** = `$MxColumn/Name`
      - Set **Status** = `$MxColumn/Status`
      - Set **MxData_MxSheet** = `$NewMxSheet`
      - Set **MxData_MxCellStyle** = `$MxCellStyle`**
3. **Call Microflow **XLSReport.SUB_Import_XPathList** (Result: **$Variable**)**
4. **Retrieve related **MxStatic_MxColumn** via Association from **$MxColumn** (Result: **$MxStaticList**)**
5. 🔄 **LOOP:** For each **$IteratorMxStatic** in **$MxStaticList**
   │ 1. **Call Microflow **XLSReport.SUB_ImportMxStatic** (Result: **$Variable**)**
   └─ **End Loop**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.