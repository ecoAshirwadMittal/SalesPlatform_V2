# Nanoflow: NA_ACT_FinalizeChanges

**Allowed Roles:** EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps

## 📥 Inputs

- **$DAHelper** (EcoATM_DA.DAHelper)
- **$DAWeek** (EcoATM_DA.DAWeek)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
2. **Call Microflow **EcoATM_DA.SUB_FinalizeDeviceAllocation****
3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
