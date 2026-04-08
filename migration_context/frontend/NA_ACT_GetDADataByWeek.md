# Nanoflow: NA_ACT_GetDADataByWeek

**Allowed Roles:** EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps

## 📥 Inputs

- **$DAHelper** (EcoATM_DA.DAHelper)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
2. **Retrieve related **DAHelper_DAWeek** via Association from **$DAHelper** (Result: **$DAWeek**)**
3. **Retrieve related **DAWeek_Week** via Association from **$DAWeek** (Result: **$Week**)**
4. **Call Microflow **EcoATM_DA.SUB_GetDADataByWeek****
5. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
6. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
