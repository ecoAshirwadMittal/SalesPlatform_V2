# Microflow Detailed Specification: VAL_Template_Edit_CSV

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$IsValid** = `true`**
2. 🔀 **DECISION:** `trim($MxTemplate/Name) != ''`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `($MxTemplate/DateTimePresentation) != empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `($MxTemplate/CSVSeparator) != empty`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$IsValid`
               ➔ **If [false]:**
                  1. **Update Variable **$IsValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🏁 **END:** Return `$IsValid`
         ➔ **If [false]:**
            1. **Update Variable **$IsValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `($MxTemplate/CSVSeparator) != empty`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$IsValid`
               ➔ **If [false]:**
                  1. **Update Variable **$IsValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🏁 **END:** Return `$IsValid`
   ➔ **If [false]:**
      1. **Update Variable **$IsValid** = `false`**
      2. **ValidationFeedback**
      3. 🔀 **DECISION:** `($MxTemplate/DateTimePresentation) != empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `($MxTemplate/CSVSeparator) != empty`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$IsValid`
               ➔ **If [false]:**
                  1. **Update Variable **$IsValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🏁 **END:** Return `$IsValid`
         ➔ **If [false]:**
            1. **Update Variable **$IsValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `($MxTemplate/CSVSeparator) != empty`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$IsValid`
               ➔ **If [false]:**
                  1. **Update Variable **$IsValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🏁 **END:** Return `$IsValid`

**Final Result:** This process concludes by returning a [Boolean] value.