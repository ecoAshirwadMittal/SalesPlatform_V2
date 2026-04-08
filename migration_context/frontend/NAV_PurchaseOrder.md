# Nanoflow: NAV_PurchaseOrder

**Allowed Roles:** EcoATM_PO.Administrator, EcoATM_PO.SalesOps

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
2. **Call Microflow **EcoATM_PO.NAV_CreatePO****
3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$HideProgress**)**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
