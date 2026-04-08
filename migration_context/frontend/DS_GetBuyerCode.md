# Nanoflow: DS_GetBuyerCode

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## ⚙️ Execution Flow

1. **Call Microflow **EcoATM_PWS.DS_BuyerCodeBySession** (Result: **$BuyerCode**)**
2. 🔀 **DECISION:** `$BuyerCode = empty`
   ➔ **If [false]:**
      1. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      2. 🏁 **END:** Return `$BuyerCode`
   ➔ **If [true]:**
      1. **Open Page: **EcoATM_PWS.PWSOrder_PE_Dashboard****
      2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      3. 🏁 **END:** Return `empty`

## 🏁 Returns
`Object`
