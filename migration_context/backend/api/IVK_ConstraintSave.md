# Microflow Detailed Specification: IVK_ConstraintSave

### 📥 Inputs (Parameters)
- **$MxConstraint** (Type: XLSReport.MxConstraint)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Commit** = `true`**
2. **Create Variable **$Value** = `''`**
3. 🔀 **DECISION:** `$MxConstraint/AttributeType`
   ➔ **If [(empty)]:**
      1. **ValidationFeedback**
      2. **Update Variable **$Commit** = `false`**
      3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$Commit`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                     ➔ **If [true]:**
                        1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                        2. **Close current page/popup**
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                        2. **AggregateList**
                        3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                        4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                        5. **Close current page/popup**
                        6. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Commit** = `false`**
            3. 🔀 **DECISION:** `$Commit`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                     ➔ **If [true]:**
                        1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                        2. **Close current page/popup**
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                        2. **AggregateList**
                        3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                        4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                        5. **Close current page/popup**
                        6. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
   ➔ **If [Decimal]:**
      1. **Update Variable **$Value** = `if $MxConstraint/Constraint != XLSReport.ConstraintType.NotEmpty and $MxConstraint/Constraint != XLSReport.ConstraintType._empty then toString($MxConstraint/ConstraintDecimal) else ''`**
      2. 🔀 **DECISION:** `$MxConstraint/ConstraintDecimal != empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$MxConstraint/Constraint != empty`
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Commit** = `false`**
                  3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Greater or $MxConstraint/Constraint = XLSReport.ConstraintType.GreaterEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Equal or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Smaller or $MxConstraint/Constraint = XLSReport.ConstraintType.SmallerEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEmpty or $MxConstraint/Constraint = XLSReport.ConstraintType._empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Commit** = `false`**
            3. 🔀 **DECISION:** `$MxConstraint/Constraint != empty`
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Commit** = `false`**
                  3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Greater or $MxConstraint/Constraint = XLSReport.ConstraintType.GreaterEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Equal or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Smaller or $MxConstraint/Constraint = XLSReport.ConstraintType.SmallerEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEmpty or $MxConstraint/Constraint = XLSReport.ConstraintType._empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
   ➔ **If [Text]:**
      1. **Update Variable **$Value** = `if $MxConstraint/Constraint != XLSReport.ConstraintType.NotEmpty and $MxConstraint/Constraint != XLSReport.ConstraintType._empty then $MxConstraint/ConstraintText else ''`**
      2. 🔀 **DECISION:** `if $MxConstraint/Constraint != XLSReport.ConstraintType.NotEmpty and $MxConstraint/Constraint != XLSReport.ConstraintType._empty then $MxConstraint/ConstraintText != empty and $MxConstraint/ConstraintText != '' else true`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$MxConstraint/Constraint != empty`
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Commit** = `false`**
                  3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Contains or $MxConstraint/Constraint = XLSReport.ConstraintType.Equal or $MxConstraint/Constraint = XLSReport.ConstraintType.StartWith or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEmpty or $MxConstraint/Constraint = XLSReport.ConstraintType._empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Commit** = `false`**
            3. 🔀 **DECISION:** `$MxConstraint/Constraint != empty`
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Commit** = `false`**
                  3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Contains or $MxConstraint/Constraint = XLSReport.ConstraintType.Equal or $MxConstraint/Constraint = XLSReport.ConstraintType.StartWith or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEmpty or $MxConstraint/Constraint = XLSReport.ConstraintType._empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
   ➔ **If [Number]:**
      1. **Update Variable **$Value** = `if $MxConstraint/Constraint != XLSReport.ConstraintType.NotEmpty and $MxConstraint/Constraint != XLSReport.ConstraintType._empty then toString($MxConstraint/ConstraintNumber) else ''`**
      2. 🔀 **DECISION:** `$MxConstraint/ConstraintNumber != empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$MxConstraint/Constraint != empty`
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Commit** = `false`**
                  3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Greater or $MxConstraint/Constraint = XLSReport.ConstraintType.GreaterEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Equal or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Smaller or $MxConstraint/Constraint = XLSReport.ConstraintType.SmallerEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEmpty or $MxConstraint/Constraint = XLSReport.ConstraintType._empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Commit** = `false`**
            3. 🔀 **DECISION:** `$MxConstraint/Constraint != empty`
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Commit** = `false`**
                  3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$Commit`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                 ➔ **If [true]:**
                                    1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                    2. **AggregateList**
                                    3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                    4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                    5. **Close current page/popup**
                                    6. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Greater or $MxConstraint/Constraint = XLSReport.ConstraintType.GreaterEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Equal or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Smaller or $MxConstraint/Constraint = XLSReport.ConstraintType.SmallerEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEmpty or $MxConstraint/Constraint = XLSReport.ConstraintType._empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
   ➔ **If [Date]:**
      1. 🔀 **DECISION:** `$MxConstraint/ConstraintDateTime != empty`
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Commit** = `false`**
            3. 🔀 **DECISION:** `$MxConstraint/ConstraintNumber != empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$MxConstraint/Constraint != empty`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Greater or $MxConstraint/Constraint = XLSReport.ConstraintType.GreaterEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Equal or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Smaller or $MxConstraint/Constraint = XLSReport.ConstraintType.SmallerEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEmpty or $MxConstraint/Constraint = XLSReport.ConstraintType._empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Commit** = `false`**
                                    3. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Commit** = `false`**
                                    3. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Commit** = `false`**
                  3. 🔀 **DECISION:** `$MxConstraint/Constraint != empty`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Greater or $MxConstraint/Constraint = XLSReport.ConstraintType.GreaterEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Equal or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Smaller or $MxConstraint/Constraint = XLSReport.ConstraintType.SmallerEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEmpty or $MxConstraint/Constraint = XLSReport.ConstraintType._empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Commit** = `false`**
                                    3. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Commit** = `false`**
                                    3. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Update Variable **$Value** = `if $MxConstraint/Constraint != XLSReport.ConstraintType.NotEmpty and $MxConstraint/Constraint != XLSReport.ConstraintType._empty then toString($MxConstraint/ConstraintNumber) + ' ' + getCaption($MxConstraint/ConstraintDateTime) + ' current Datetime' else ''`**
            2. 🔀 **DECISION:** `$MxConstraint/ConstraintNumber != empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$MxConstraint/Constraint != empty`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Greater or $MxConstraint/Constraint = XLSReport.ConstraintType.GreaterEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Equal or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Smaller or $MxConstraint/Constraint = XLSReport.ConstraintType.SmallerEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEmpty or $MxConstraint/Constraint = XLSReport.ConstraintType._empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Commit** = `false`**
                                    3. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Commit** = `false`**
                                    3. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Commit** = `false`**
                  3. 🔀 **DECISION:** `$MxConstraint/Constraint != empty`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Commit** = `false`**
                        3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$Commit`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                       ➔ **If [true]:**
                                          1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          2. **Close current page/popup**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                          2. **AggregateList**
                                          3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                          4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                          5. **Close current page/popup**
                                          6. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Greater or $MxConstraint/Constraint = XLSReport.ConstraintType.GreaterEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Equal or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.Smaller or $MxConstraint/Constraint = XLSReport.ConstraintType.SmallerEqual or $MxConstraint/Constraint = XLSReport.ConstraintType.NotEmpty or $MxConstraint/Constraint = XLSReport.ConstraintType._empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Commit** = `false`**
                                    3. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Commit** = `false`**
                              3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Commit** = `false`**
                                    3. 🔀 **DECISION:** `$Commit`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                                             ➔ **If [true]:**
                                                1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                                                2. **AggregateList**
                                                3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                                                4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                                                5. **Close current page/popup**
                                                6. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
   ➔ **If [YesNo]:**
      1. 🔀 **DECISION:** `$MxConstraint/Constraint = XLSReport.ConstraintType.Equal`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Commit`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                           ➔ **If [true]:**
                              1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                              2. **Close current page/popup**
                              3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                              2. **AggregateList**
                              3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                              4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                              5. **Close current page/popup**
                              6. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Commit** = `false`**
                  3. 🔀 **DECISION:** `$Commit`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                           ➔ **If [true]:**
                              1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                              2. **Close current page/popup**
                              3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                              2. **AggregateList**
                              3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                              4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                              5. **Close current page/popup**
                              6. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Commit** = `false`**
            3. 🔀 **DECISION:** `$MxConstraint/AndOr != empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Commit`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                           ➔ **If [true]:**
                              1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                              2. **Close current page/popup**
                              3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                              2. **AggregateList**
                              3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                              4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                              5. **Close current page/popup**
                              6. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Commit** = `false`**
                  3. 🔀 **DECISION:** `$Commit`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MxConstraint/Sequence > 0`
                           ➔ **If [true]:**
                              1. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                              2. **Close current page/popup**
                              3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **DB Retrieve **XLSReport.MxConstraint** Filter: `[XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet] [id != $MxConstraint]` (Result: **$MxConstraintList**)**
                              2. **AggregateList**
                              3. **Update **$MxConstraint**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                              4. **Update **$MxConstraint** (and Save to DB)
      - Set **Summary** = `$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value`**
                              5. **Close current page/popup**
                              6. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.