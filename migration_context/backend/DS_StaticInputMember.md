# Microflow Detailed Specification: DS_StaticInputMember

### 📥 Inputs (Parameters)
- **$MxStatic** (Type: XLSReport.MxStatic)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxData_MxSheet** via Association from **$MxStatic** (Result: **$MxSheet**)**
2. **Retrieve related **MxSheet_Template** via Association from **$MxSheet** (Result: **$MxTemplate**)**
3. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType = $MxTemplate/XLSReport.MxTemplate_InputObject]` (Result: **$MxObjectMemberList**)**
4. 🏁 **END:** Return `$MxObjectMemberList`

**Final Result:** This process concludes by returning a [List] value.