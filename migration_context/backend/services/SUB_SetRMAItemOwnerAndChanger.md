# Microflow Detailed Specification: SUB_SetRMAItemOwnerAndChanger

### 📥 Inputs (Parameters)
- **$RMAItemList** (Type: EcoATM_RMA.RMAItem)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. 🔄 **LOOP:** For each **$IteratorRMAItem** in **$RMAItemList**
   │ 1. 🔀 **DECISION:** `isNew($IteratorRMAItem)`
   │    ➔ **If [true]:**
   │       1. **Update **$IteratorRMAItem**
      - Set **EntityOwner** = `$currentUser/Name`
      - Set **EntityChanger** = `$currentUser/Name`**
   │       2. **Add **$$IteratorRMAItem** to/from list **$RMAItemList_toCommit****
   │    ➔ **If [false]:**
   │       1. **DB Retrieve **System.User** Filter: `[id=$IteratorRMAItem/System.owner]` (Result: **$User**)**
   │       2. **Update **$IteratorRMAItem**
      - Set **EntityOwner** = `if $User!=empty then $User/Name else $currentUser/Name`
      - Set **EntityChanger** = `$currentUser/Name`**
   │       3. **Add **$$IteratorRMAItem** to/from list **$RMAItemList_toCommit****
   └─ **End Loop**
3. 🏁 **END:** Return `$RMAItemList_toCommit`

**Final Result:** This process concludes by returning a [List] value.