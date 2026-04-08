# Nanoflow: ACT_DynamicMenu_PWS

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$NavigationMenu** (EcoATM_PWS.NavigationMenu)

## ⚙️ Execution Flow

1. **Call Nanoflow **EcoATM_Direct_Theme.DS_GetOrSetBuyerCode_SessionAndTabHelper** (Result: **$BuyerCode_SessionAndTabHelper**)**
2. **Call JS Action **EcoATM_PWS.JS_RunMicroflow** (Result: **$IsSuccess**)**
3. 🔀 **DECISION:** `$IsSuccess`
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `Error loading {1} {2}`**
      2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      3. 🏁 **END:** Return empty

## ⚠️ Error Handling

- On error in **Call JS Action **EcoATM_PWS.JS_RunMicroflow** (Result: **$IsSuccess**)** → ExclusiveMerge

## 🏁 Returns
`Void`
