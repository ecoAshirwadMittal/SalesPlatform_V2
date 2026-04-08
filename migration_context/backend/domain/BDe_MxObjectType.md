# Microflow Detailed Specification: BDe_MxObjectType

### 📥 Inputs (Parameters)
- **$MxObjectType** (Type: MxModelReflection.MxObjectType)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxObjectReference_MxObjectType_Parent** via Association from **$MxObjectType** (Result: **$MxObjectReferenceList_Parent**)**
2. 🔄 **LOOP:** For each **$Reference** in **$MxObjectReferenceList_Parent**
   │ 1. 🔀 **DECISION:** `$Reference/MxModelReflection.MxObjectReference_MxObjectType_Child = empty`
   │    ➔ **If [false]:**
   │       1. **Retrieve related **MxObjectReference_MxObjectType_Parent** via Association from **$Reference** (Result: **$MxObjectReferenceList**)**
   │       2. **AggregateList**
   │       3. 🔀 **DECISION:** `$count = 1`
   │          ➔ **If [true]:**
   │             1. **Delete**
   │          ➔ **If [false]:**
   │    ➔ **If [true]:**
   │       1. **Delete**
   └─ **End Loop**
3. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.