# Microflow Detailed Specification: SUB_SetBuyerCodeOwnerAndChanger

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `isNew($BuyerCode)`
   ➔ **If [true]:**
      1. **Update **$BuyerCode**
      - Set **EntityOwner** = `$currentUser/Name`
      - Set **EntityChanger** = `$currentUser/Name`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **DB Retrieve **System.User** Filter: `[id=$BuyerCode/System.owner]` (Result: **$User**)**
      2. **Update **$BuyerCode**
      - Set **EntityChanger** = `$currentUser/Name`
      - Set **EntityOwner** = `if $User!= empty then $User/Name else $currentUser/Name`**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.