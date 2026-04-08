# Microflow Detailed Specification: IVK_ConstraintSaveAndNext

### 📥 Inputs (Parameters)
- **$MxConstraint** (Type: XLSReport.MxConstraint)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxConstraint_MxXPath** via Association from **$MxConstraint** (Result: **$MxXPath**)**
2. 🔀 **DECISION:** `$MxXPath = empty`
   ➔ **If [false]:**
      1. **Retrieve related **MxConstraint_MxSheet** via Association from **$MxConstraint** (Result: **$MxSheet**)**
      2. **DB Retrieve **XLSReport.MxReferenceHandling** Filter: `[XLSReport.MxReferenceHandling_MxSheet = $MxSheet]` (Result: **$MxReferenceHandlingList**)**
      3. **Call Microflow **XLSReport.XPath_Validate** (Result: **$XpathCommit**)**
      4. 🔀 **DECISION:** `$XpathCommit`
         ➔ **If [true]:**
            1. **Commit/Save **$MxReferenceHandlingList** to Database**
            2. **Call Microflow **XLSReport.XPath_Delete_Subs****
            3. **Call Microflow **XLSReport.SF_FindAttribute** (Result: **$Attribute**)**
            4. **Call Microflow **XLSReport.SF_ParseDateType** (Result: **$AttributeType**)**
            5. **Update **$MxConstraint**
      - Set **Attribute** = `$Attribute/AttributeName`
      - Set **AttributeType** = `$AttributeType`
      - Set **Summary** = `$Attribute/CompleteName`
      - Set **Constraint** = `if $AttributeType = XLSReport.AttributeType.YesNo then XLSReport.ConstraintType.Equal else $MxConstraint/Constraint`**
            6. **Close current page/popup**
            7. **Maps to Page: **XLSReport.MxConstraint_Edit****
            8. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Create **XLSReport.MxXPath** (Result: **$NewMxXPath**)
      - Set **MxConstraint_MxXPath** = `$MxConstraint`**
      2. **Update **$MxConstraint**
      - Set **MxConstraint_MxXPath** = `$NewMxXPath`**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.