# Nanoflow: NA_ACT_ConfirmReviewChanges

**Allowed Roles:** EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps

## 📥 Inputs

- **$DAHelper** (EcoATM_DA.DAHelper)
- **$DAWeek** (EcoATM_DA.DAWeek)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `$DAWeek/IsFinalized = false`
   ➔ **If [false]:**
      1. **Show Message (Information): `Device allocation for this week has already been finalized.`**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
      2. **Call Microflow **EcoATM_DA.SUB_ConfirmReviewChanges****
      3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
