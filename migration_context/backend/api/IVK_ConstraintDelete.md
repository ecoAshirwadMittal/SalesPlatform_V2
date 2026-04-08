# Microflow Detailed Specification: IVK_ConstraintDelete

### 📥 Inputs (Parameters)
- **$MxConstraint** (Type: XLSReport.MxConstraint)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [Sequence > $MxConstraint/Sequence]` (Result: **$MxConstraintList**)**
2. 🔄 **LOOP:** For each **$HigherConstraint** in **$MxConstraintList**
   │ 1. **Update **$HigherConstraint**
      - Set **Sequence** = `$HigherConstraint/Sequence - 1`**
   └─ **End Loop**
3. **Commit/Save **$MxConstraintList** to Database**
4. **Delete**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.