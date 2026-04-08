# Nanoflow: DS_GetBuyerCode_SessionAndTabHelper

**Allowed Roles:** EcoATM_Direct_Theme.Administrator, EcoATM_Direct_Theme.User

## ⚙️ Execution Flow

1. **Call JS Action **EcoATM_Direct_Theme.JSA_GetTabIndex** (Result: **$TabId**)**
2. **Create Variable **$Attempt** = `1`**
3. **Call Microflow **EcoATM_Direct_Theme.SUB_GetBuyerCode_SessionAndTabHelper** (Result: **$BuyerCode_SessionAndTabHelper**)**
4. 🔀 **DECISION:** `$BuyerCode_SessionAndTabHelper != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$BuyerCode_SessionAndTabHelper`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$Attempt > 3`
         ➔ **If [false]:**
            1. **Update Variable **$Attempt** = `$Attempt +1`**
            2. **Call JS Action **NanoflowCommons.Wait** (Result: **$Variable**)**
               *(Merging with existing path logic)*
         ➔ **If [true]:**
            1. 🏁 **END:** Return `empty`

## 🏁 Returns
`Object`
