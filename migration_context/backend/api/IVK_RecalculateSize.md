# Microflow Detailed Specification: IVK_RecalculateSize

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MxModelReflection.DbSizeEstimate** Filter: `[NrOfRecords!=empty] [MxModelReflection.DbSizeEstimate_MxObjectType/MxModelReflection.MxObjectType]` (Result: **$DbSizeEstimateList**)**
2. 🔄 **LOOP:** For each **$Estimate** in **$DbSizeEstimateList**
   │ 1. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType/MxModelReflection.MxObjectType/MxModelReflection.DbSizeEstimate_MxObjectType = $Estimate]` (Result: **$MxObjectMemberList**)**
   │ 2. **AggregateList**
   │ 3. **Create Variable **$Size** = `8 + $count`**
   │ 4. 🔄 **LOOP:** For each **$Member** in **$MxObjectMemberList**
   │    │ 1. **Retrieve related **MxObjectMember_Type** via Association from **$Member** (Result: **$ValueType**)**
   │    │ 2. 🔀 **DECISION:** `$ValueType/TypeEnum`
   │    │    ➔ **If [AutoNumber]:**
   │    │       1. **Update Variable **$Size** = `$Size + 8`**
   │    │    ➔ **If [LongType]:**
   │    │       1. **Update Variable **$Size** = `$Size + 8`**
   │    │    ➔ **If [DateTime]:**
   │    │       1. **Update Variable **$Size** = `$Size + 8`**
   │    │    ➔ **If [(empty)]:**
   │    │       1. 🔀 **DECISION:** `contains( toLowerCase($Member/CompleteName), 'owner') or contains( toLowerCase($Member/CompleteName), 'changedby')`
   │    │          ➔ **If [true]:**
   │    │             1. **Update Variable **$Size** = `$Size + 8`**
   │    │          ➔ **If [false]:**
   │    │    ➔ **If [IntegerType]:**
   │    │       1. **Update Variable **$Size** = `$Size + 4`**
   │    │    ➔ **If [BooleanType]:**
   │    │       1. **Update Variable **$Size** = `$Size + 1`**
   │    │    ➔ **If [EnumType]:**
   │    │       1. **Update Variable **$Size** = `$Size + 2+(20*2)`**
   │    │    ➔ **If [HashString]:**
   │    │       1. **Update Variable **$Size** = `$Size + 2+(68*2)`**
   │    │    ➔ **If [StringType]:**
   │    │       1. **Update Variable **$Size** = `$Size + 2+( ceil(if $Member/FieldLength != empty then $Member/FieldLength * 0.8 else 200) *2)`**
   │    │    ➔ **If [ObjectType]:**
   │    │       1. 🔀 **DECISION:** `contains( toLowerCase($Member/CompleteName), 'owner') or contains( toLowerCase($Member/CompleteName), 'changedby')`
   │    │          ➔ **If [true]:**
   │    │             1. **Update Variable **$Size** = `$Size + 8`**
   │    │          ➔ **If [false]:**
   │    │    ➔ **If [ObjectList]:**
   │    │       1. 🔀 **DECISION:** `contains( toLowerCase($Member/CompleteName), 'owner') or contains( toLowerCase($Member/CompleteName), 'changedby')`
   │    │          ➔ **If [true]:**
   │    │             1. **Update Variable **$Size** = `$Size + 8`**
   │    │          ➔ **If [false]:**
   │    │    ➔ **If [Decimal]:**
   │    │       1. **Update Variable **$Size** = `$Size + 36`**
   │    │       2. **Call Microflow **MxModelReflection.Log** (Result: **$LogProduct**)**
   │    │       3. **Update Variable **$Size** = `$Size + ceil($LogProduct:8 )`**
   │    │    ➔ **If [Currency]:**
   │    │       1. 🔀 **DECISION:** `contains( toLowerCase($Member/CompleteName), 'owner') or contains( toLowerCase($Member/CompleteName), 'changedby')`
   │    │          ➔ **If [true]:**
   │    │             1. **Update Variable **$Size** = `$Size + 8`**
   │    │          ➔ **If [false]:**
   │    │    ➔ **If [FloatType]:**
   │    │       1. 🔀 **DECISION:** `contains( toLowerCase($Member/CompleteName), 'owner') or contains( toLowerCase($Member/CompleteName), 'changedby')`
   │    │          ➔ **If [true]:**
   │    │             1. **Update Variable **$Size** = `$Size + 8`**
   │    │          ➔ **If [false]:**
   │    └─ **End Loop**
   │ 5. **Update **$Estimate**
      - Set **CalculatedSizeInBytes** = `$Size`
      - Set **CalculatedSizeInKiloBytes** = `ceil($Size : 1024)`**
   └─ **End Loop**
3. **Commit/Save **$DbSizeEstimateList** to Database**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.