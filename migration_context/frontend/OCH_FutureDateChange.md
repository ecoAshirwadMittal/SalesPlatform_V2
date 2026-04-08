# Nanoflow: OCH_FutureDateChange

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader

## 📥 Inputs

- **$MDMFuturePriceHelper** (EcoATM_PWS.MDMFuturePriceHelper)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
2. **Call Microflow **EcoATM_PWS.SUB_FutureDateChange** (Result: **$IsFutureDateValid**)**
3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
