# Nanoflow: DS_PWSSiteBanner

**Allowed Roles:** EcoATM_Direct_Theme.Administrator, EcoATM_Direct_Theme.User

## ⚙️ Execution Flow

1. **Create Variable **$currentDateTime** = `[%CurrentDateTime%]`**
2. **DB Retrieve **EcoATM_PWS.MaintenanceMode** Filter: `[IsEnabled]` (Result: **$MaintenanceModeList_Enabled**)**
3. **DB Retrieve **EcoATM_PWS.MaintenanceMode** Filter: `[IsEnabled and ('[%CurrentDateTime%]' > StartTime or '[%CurrentDateTime%]' > BannerStartTime)]` (Result: **$MaintenanceModeList**)**
4. **List Operation: **FilterByExpression** on **$MaintenanceModeList** where `[%CurrentDateTime%] > $currentObject/StartTime and [%CurrentDateTime%] < $currentObject/EndTime` (Result: **$MaintenanceModeList_MM**)**
5. 🔀 **DECISION:** `$MaintenanceModeList_MM != empty`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$MaintenanceModeList_MM** (Result: **$MaintenanceMode**)**
      2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      3. **Open Page: **EcoATM_PWS.MaintenanceMode****
      4. 🏁 **END:** Return `$MaintenanceMode`
   ➔ **If [false]:**
      1. **List Operation: **FindByExpression** on **$MaintenanceModeList** where `$currentObject/BannerStartTime != empty and [%CurrentDateTime%] > $currentObject/BannerStartTime and [%CurrentDateTime%] < $currentObject/StartTime` (Result: **$MaintenanceMode_Banner**)**
      2. 🏁 **END:** Return `$MaintenanceMode_Banner`

## 🏁 Returns
`Object`
