# Microflow Detailed Specification: Sub_OQL_GetGUID

### 📥 Inputs (Parameters)
- **$OQL** (Type: Variable)
- **$MaxBatches** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$GUID** = `0`**
2. 🔀 **DECISION:** `$MaxBatches>0`
   ➔ **If [true]:**
      1. **Create Variable **$OQL_Min** = `'select min(ObjectId) as ObjectId from ( ' + $OQL + ' ) t'`**
      2. **JavaCallAction**
      3. **List Operation: **Head** on **$undefined** (Result: **$BatchObject_Min**)**
      4. **Update Variable **$GUID** = `if $BatchObject_Min/ObjectId!=empty then $BatchObject_Min/ObjectId-1 else empty`**
      5. 🏁 **END:** Return `$GUID`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$GUID`

**Final Result:** This process concludes by returning a [Integer] value.