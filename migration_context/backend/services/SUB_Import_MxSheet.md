# Microflow Detailed Specification: SUB_Import_MxSheet

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)
- **$NewMxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxSheet_Template** via Association from **$MxTemplate** (Result: **$MxSheetList**)**
2. **CreateList**
3. 🔄 **LOOP:** For each **$IteratorMxSheet** in **$MxSheetList**
   │ 1. **Retrieve related **MxSheet_DefaultStyle** via Association from **$IteratorMxSheet** (Result: **$DefaultCellStyle**)**
   │ 2. **Retrieve related **MxSheet_HeaderStyle** via Association from **$IteratorMxSheet** (Result: **$HeaderCellStyle**)**
   │ 3. **DB Retrieve **MxModelReflection.MxObjectType** Filter: `[XLSReport.MxSheet_RowObject = $IteratorMxSheet]` (Result: **$MxObjectType**)**
   │ 4. **DB Retrieve **MxModelReflection.MxObjectReference** Filter: `[XLSReport.MxSheet_MxObjectReference = $IteratorMxSheet]` (Result: **$MxObjectReference**)**
   │ 5. **Create **XLSReport.MxSheet** (Result: **$NewMxSheet**)
      - Set **Sequence** = `$IteratorMxSheet/Sequence`
      - Set **Name** = `$IteratorMxSheet/Name`
      - Set **DataUsage** = `$IteratorMxSheet/DataUsage`
      - Set **Status** = `$IteratorMxSheet/Status`
      - Set **DistinctData** = `$IteratorMxSheet/DistinctData`
      - Set **StartRow** = `$IteratorMxSheet/StartRow`
      - Set **ColumnWidthDefault** = `$IteratorMxSheet/ColumnWidthDefault`
      - Set **ColumnWidthPixels** = `$IteratorMxSheet/ColumnWidthPixels`
      - Set **RowHeightDefault** = `$IteratorMxSheet/RowHeightDefault`
      - Set **RowHeightPoint** = `$IteratorMxSheet/RowHeightPoint`
      - Set **FormLayout_GroupBy** = `$IteratorMxSheet/FormLayout_GroupBy`
      - Set **MxSheet_Template** = `$NewMxTemplate`
      - Set **MxSheet_DefaultStyle** = `$DefaultCellStyle`
      - Set **MxSheet_HeaderStyle** = `$HeaderCellStyle`
      - Set **MxSheet_RowObject** = `$MxObjectType`
      - Set **MxSheet_MxObjectReference** = `$MxObjectReference`**
   │ 6. **Add **$$NewMxSheet** to/from list **$MxSheetCommitList****
   │ 7. **Call Microflow **XLSReport.SUB_Import_MxReferenceHandling** (Result: **$Variable**)**
   │ 8. **Call Microflow **XLSReport.SUB_Import_MxColumnSettings** (Result: **$Variable**)**
   │ 9. **Call Microflow **XLSReport.SUB_Import_MxRowSettings** (Result: **$Variable**)**
   │ 10. **Call Microflow **XLSReport.SUB_Import_MxSorting** (Result: **$Variable**)**
   │ 11. **Call Microflow **XLSReport.SUB_Import_MxConstraint** (Result: **$Variable**)**
   │ 12. **Call Microflow **XLSReport.SUB_Import_MxData** (Result: **$Variable**)**
   └─ **End Loop**
4. **Commit/Save **$MxSheetCommitList** to Database**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.