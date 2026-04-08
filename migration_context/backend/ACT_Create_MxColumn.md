# Microflow Detailed Specification: ACT_Create_MxColumn

### 📥 Inputs (Parameters)
- **$MappingParent** (Type: XLSReport.MxSheet)
- **$Name** (Type: Variable)
- **$Status** (Type: Variable)
- **$ColumnNumber** (Type: Variable)
- **$ObjectAttribute** (Type: Variable)
- **$DataAggregate** (Type: Variable)
- **$DataAggregateFunction** (Type: Variable)
- **$ResultAggregate** (Type: Variable)
- **$ResultAggregateFunction** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxData_MxSheet** via Association from **$MappingParent** (Result: **$MxDataList**)**
2. 🔀 **DECISION:** `$MxDataList = empty`
   ➔ **If [true]:**
      1. **Create **XLSReport.MxColumn** (Result: **$NewMxColumn**)
      - Set **ColumnNumber** = `$ColumnNumber`
      - Set **ObjectAttribute** = `$ObjectAttribute`
      - Set **DataAggregate** = `$DataAggregate`
      - Set **ResultAggregate** = `$ResultAggregate`
      - Set **Name** = `$Name`
      - Set **MxData_MxSheet** = `$MappingParent`**
      2. 🏁 **END:** Return `$NewMxColumn`
   ➔ **If [false]:**
      1. **List Operation: **Find** on **$undefined** where `$Name` (Result: **$NewMxData**)**

**Final Result:** This process concludes by returning a [Object] value.