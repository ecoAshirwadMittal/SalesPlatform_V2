# Microflow Detailed Specification: DS_First_Attribute

### 📥 Inputs (Parameters)
- **$MxXpath** (Type: XLSReport.MxXPath)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxXPath_MxData** via Association from **$MxXpath** (Result: **$MxData**)**
2. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType/MxModelReflection.MxObjectType/XLSReport.MxSheet_RowObject = $MxData/XLSReport.MxData_MxSheet]` (Result: **$MxObjectMemberList**)**
3. 🏁 **END:** Return `$MxObjectMemberList`

**Final Result:** This process concludes by returning a [List] value.