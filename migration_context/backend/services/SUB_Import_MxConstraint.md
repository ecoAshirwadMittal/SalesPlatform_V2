# Microflow Detailed Specification: SUB_Import_MxConstraint

### 📥 Inputs (Parameters)
- **$MxSheet** (Type: XLSReport.MxSheet)
- **$NewMxSheet** (Type: XLSReport.MxSheet)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxConstraint_MxSheet** via Association from **$MxSheet** (Result: **$MxConstraintList**)**
2. 🔄 **LOOP:** For each **$IteratorMxConstraint** in **$MxConstraintList**
   │ 1. **Create **XLSReport.MxConstraint** (Result: **$NewMxConstraint**)
      - Set **Sequence** = `$IteratorMxConstraint/Sequence`
      - Set **Summary** = `$IteratorMxConstraint/Summary`
      - Set **Attribute** = `$IteratorMxConstraint/Attribute`
      - Set **Constraint** = `$IteratorMxConstraint/Constraint`
      - Set **AttributeType** = `$IteratorMxConstraint/AttributeType`
      - Set **ConstraintText** = `$IteratorMxConstraint/ConstraintText`
      - Set **ConstraintNumber** = `$IteratorMxConstraint/ConstraintNumber`
      - Set **ConstraintDecimal** = `$IteratorMxConstraint/ConstraintDecimal`
      - Set **ConstraintDateTime** = `$IteratorMxConstraint/ConstraintDateTime`
      - Set **ConstraintBoolean** = `$IteratorMxConstraint/ConstraintBoolean`
      - Set **AndOr** = `$IteratorMxConstraint/AndOr`
      - Set **MxConstraint_MxSheet** = `$NewMxSheet`
      - Set **ConstraintDecimal** = `$IteratorMxConstraint/ConstraintDecimal`**
   │ 2. **Retrieve related **MxConstraint_MxXPath** via Association from **$NewMxConstraint** (Result: **$MxXPath**)**
   │ 3. **Delete**
   │ 4. **Update **$NewMxConstraint** (and Save to DB)
      - Set **MxConstraint_MxXPath** = `empty`**
   │ 5. **Call Microflow **XLSReport.SUB_Import_XPathList** (Result: **$Variable**)**
   └─ **End Loop**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.