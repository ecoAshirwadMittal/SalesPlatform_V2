# Microflow Detailed Specification: DS_Constraint_attribute

### 📥 Inputs (Parameters)
- **$MxXpath** (Type: XLSReport.MxXPath)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxConstraint_MxXPath** via Association from **$MxXpath** (Result: **$MxConstraint**)**
2. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType/MxModelReflection.MxObjectType/XLSReport.MxSheet_RowObject = $MxConstraint/XLSReport.MxConstraint_MxSheet]` (Result: **$MxObjectMemberList**)**
3. 🏁 **END:** Return `$MxObjectMemberList`

**Final Result:** This process concludes by returning a [List] value.