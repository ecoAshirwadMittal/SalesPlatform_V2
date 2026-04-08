# Microflow Detailed Specification: SUB_Import_XPathRecursion

### 📥 Inputs (Parameters)
- **$NewMxData** (Type: XLSReport.MxData)
- **$MxXPathList** (Type: XLSReport.MxXPath)
- **$ParentMxXPath** (Type: XLSReport.MxXPath)
- **$MxSorting** (Type: XLSReport.MxSorting)
- **$MxConstraint** (Type: XLSReport.MxConstraint)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$MxXPathList != empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$MxXPath**)**
      2. **Retrieve related **MxXPath_MxObjectMember** via Association from **$MxXPath** (Result: **$MxObjectMember**)**
      3. **Retrieve related **MxXPath_MxObjectReference** via Association from **$MxXPath** (Result: **$MxObjectReference**)**
      4. **Retrieve related **MxXPath_MxObjectType** via Association from **$MxXPath** (Result: **$MxObjectType**)**
      5. **Create **XLSReport.MxXPath** (Result: **$NewChildMxXPath**)
      - Set **RetrieveType** = `$MxXPath/RetrieveType`
      - Set **SubVisible** = `$MxXPath/SubVisible`
      - Set **MxXPath_MxData** = `$NewMxData`
      - Set **MxXPath_MxObjectMember** = `$MxObjectMember`
      - Set **MxXPath_MxObjectReference** = `$MxObjectReference`
      - Set **MxXPath_MxObjectType** = `$MxObjectType`
      - Set **ChildMxXPath_MxXPath** = `$ParentMxXPath`
      - Set **MxConstraint_MxXPath** = `$MxConstraint`
      - Set **MxSorting_MxXPath** = `$MxSorting`**
      6. 🔀 **DECISION:** `$ParentMxXPath != empty`
         ➔ **If [true]:**
            1. **Update **$ParentMxXPath** (and Save to DB)
      - Set **MxXPath_ParentMxXPath** = `$NewChildMxXPath`**
            2. **List Operation: **Tail** on **$undefined** (Result: **$MxXPathList_Rest**)**
            3. **Call Microflow **XLSReport.SUB_Import_XPathRecursion** (Result: **$Variable**)**
            4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **List Operation: **Tail** on **$undefined** (Result: **$MxXPathList_Rest**)**
            2. **Call Microflow **XLSReport.SUB_Import_XPathRecursion** (Result: **$Variable**)**
            3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.