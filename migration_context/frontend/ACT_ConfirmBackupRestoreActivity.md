# Nanoflow: ACT_ConfirmBackupRestoreActivity

**Allowed Roles:** AuctionUI.Administrator

## ⚙️ Execution Flow

1. **Call Microflow **AuctionUI.SUB_GetAppURL** (Result: **$AppUrl**)**
2. 🔀 **DECISION:** `contains($AppUrl,'qa') or contains($AppUrl,'dev') or contains($AppUrl,'localhost')`
   ➔ **If [true]:**
      1. **Call JS Action **NanoflowCommons.ShowConfirmation** (Result: **$Confirmation**)**
      2. 🔀 **DECISION:** `$Confirmation`
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Restore**)**
            2. **Call Microflow **AuctionUI.Sub_PerformRestoreActivity****
            3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
            4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `Backup Restoration not enabled for environment`**
      2. 🏁 **END:** Return empty

## ⚠️ Error Handling

- On error in **Call Microflow **AuctionUI.Sub_PerformRestoreActivity**** → Show Message (Error): `Error Restoring Configurations : {1}`

## 🏁 Returns
`Void`
