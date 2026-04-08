# Microflow Detailed Specification: SUB_SetRMAOwnerAndChanger

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `isNew($RMA)`
   ➔ **If [true]:**
      1. **Update **$RMA**
      - Set **EntityOwner** = `$currentUser/Name`
      - Set **EntityChanger** = `$currentUser/Name`
      - Set **SystemStatus** = `$RMA/EcoATM_RMA.RMA_RMAStatus/EcoATM_RMA.RMAStatus/SystemStatus`**
      2. **Commit/Save **$RMA** to Database**
      3. **Retrieve related **RMAItem_RMA** via Association from **$RMA** (Result: **$RMAItemList**)**
      4. **Call Microflow **EcoATM_RMA.SUB_SetRMAItemOwnerAndChanger** (Result: **$RMAItemList_toCommit**)**
      5. **Commit/Save **$RMAItemList_toCommit** to Database**
      6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **DB Retrieve **System.User** Filter: `[id=$RMA/System.owner]` (Result: **$User**)**
      2. **Update **$RMA**
      - Set **EntityOwner** = `if $User!=empty then $User/Name else $currentUser/Name`
      - Set **EntityChanger** = `$currentUser/Name`**
      3. **Commit/Save **$RMA** to Database**
      4. **Retrieve related **RMAItem_RMA** via Association from **$RMA** (Result: **$RMAItemList**)**
      5. **Call Microflow **EcoATM_RMA.SUB_SetRMAItemOwnerAndChanger** (Result: **$RMAItemList_toCommit**)**
      6. **Commit/Save **$RMAItemList_toCommit** to Database**
      7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.