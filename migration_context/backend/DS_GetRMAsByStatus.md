# Microflow Detailed Specification: DS_GetRMAsByStatus

### 📥 Inputs (Parameters)
- **$RMAStatusList** (Type: EcoATM_RMA.RMAStatus)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$RMAStatusList != empty`
   ➔ **If [true]:**
      1. **CreateList**
      2. 🔄 **LOOP:** For each **$IteratorRMAStatus** in **$RMAStatusList**
         │ 1. **Retrieve related **RMA_RMAStatus** via Association from **$IteratorRMAStatus** (Result: **$RMAList_Iterator**)**
         │ 2. **Add **$$RMAList_Iterator** to/from list **$RMAList****
         └─ **End Loop**
      3. 🏁 **END:** Return `$RMAList`
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_RMA.RMA**  (Result: **$RMAList_All**)**
      2. 🏁 **END:** Return `$RMAList_All`

**Final Result:** This process concludes by returning a [List] value.