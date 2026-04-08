# Microflow Detailed Specification: ACT_CreateDriveItem

### 📥 Inputs (Parameters)
- **$List** (Type: Sharepoint.List)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SelectedList** via Association from **$List** (Result: **$Explorer**)**
2. **Retrieve related **List_Drive** via Association from **$List** (Result: **$Drive**)**
3. **Retrieve related **SelectedSite** via Association from **$Explorer** (Result: **$Site**)**
4. **Create **Sharepoint.DriveItemContents** (Result: **$NewDriveItemContents**)**
5. **Create **Sharepoint.ListItem** (Result: **$NewListItem**)
      - Set **ListItem_Explorer** = `$Explorer`**
6. **Create **Sharepoint.DriveItem** (Result: **$NewDriveItem**)
      - Set **DriveItem_ListItem** = `$NewListItem`
      - Set **DriveItem_DriveItemContents** = `$NewDriveItemContents`**
7. **Create **Sharepoint.CreateDriveItemHelper** (Result: **$NewCreateDriveItemHelper**)
      - Set **CreateDriveItemHelper_DriveItem** = `$NewDriveItem`
      - Set **CreateDriveItemHelper_Explorer** = `$Explorer`
      - Set **DriveId** = `$Drive/_id`
      - Set **SiteId** = `$Site/_id`
      - Set **ParentId** = `empty`
      - Set **ListId** = `$List/_id`**
8. **Maps to Page: **Sharepoint.DriveItem_Create****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.