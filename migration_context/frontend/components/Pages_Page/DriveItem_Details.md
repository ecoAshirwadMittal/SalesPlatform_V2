# Page: DriveItem_Details

**Allowed Roles:** Sharepoint.Administrator

**Layout:** `Sharepoint.DefaultLayout`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [acti] → **Microflow**: `Sharepoint.ACT_CloseDriveItem`
  - 📦 **DataGrid** [MF: Sharepoint.DS_GetDriveItemsForDriveItem]
      ↳ [acti] → **Page**: `Sharepoint.DriveItem_Details`
    - 📊 **Column**: id [Width: 20]
    - 📊 **Column**: Name [Width: 58]
    - 📊 **Column**: Size [Width: 22]
