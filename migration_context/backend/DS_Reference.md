# Microflow Detailed Specification: DS_Reference

### 📥 Inputs (Parameters)
- **$MxXPath** (Type: XLSReport.MxXPath)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **ChildMxXPath_MxXPath** via Association from **$MxXPath** (Result: **$ParentMxXPath**)**
2. **DB Retrieve **MxModelReflection.MxObjectReference** Filter: `[MxModelReflection.MxObjectReference_MxObjectType = $ParentMxXPath/XLSReport.MxXPath_MxObjectType]` (Result: **$MxObjectReferenceList**)**
3. 🏁 **END:** Return `$MxObjectReferenceList`

**Final Result:** This process concludes by returning a [List] value.