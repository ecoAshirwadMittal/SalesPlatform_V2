# Microflow Detailed Specification: IVK_Constraint_Higher

### 📥 Inputs (Parameters)
- **$MxConstraint** (Type: XLSReport.MxConstraint)
- **$MxSheet** (Type: XLSReport.MxSheet)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxSheet] [Sequence > $MxConstraint/Sequence]` (Result: **$HigherMxConstraint**)**
2. 🔀 **DECISION:** `$HigherMxConstraint != empty`
   ➔ **If [true]:**
      1. **Update **$HigherMxConstraint** (and Save to DB)
      - Set **Sequence** = `$HigherMxConstraint/Sequence - 1`**
      2. **Update **$MxConstraint** (and Save to DB)
      - Set **Sequence** = `$HigherMxConstraint/Sequence + 1`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.