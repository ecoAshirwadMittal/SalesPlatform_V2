# Microflow Detailed Specification: DS_Constraint_reference

### 📥 Inputs (Parameters)
- **$MxXpath** (Type: XLSReport.MxXPath)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxConstraint_MxXPath** via Association from **$MxXpath** (Result: **$MxConstraint**)**
2. **DB Retrieve **MxModelReflection.MxObjectReference** Filter: `[MxModelReflection.MxObjectReference_MxObjectType/MxModelReflection.MxObjectType/XLSReport.MxSheet_RowObject = $MxConstraint/XLSReport.MxConstraint_MxSheet]` (Result: **$MxObjectReferenceList**)**
3. 🏁 **END:** Return `$MxObjectReferenceList`

**Final Result:** This process concludes by returning a [List] value.