# Microflow Detailed Specification: DS_GetDriveItemsForDriveItem

### 📥 Inputs (Parameters)
- **$DriveItem** (Type: Sharepoint.DriveItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **ListedDriveItems** via Association from **$DriveItem** (Result: **$Explorer**)**
2. **Retrieve related **Explorer_Authorization** via Association from **$Explorer** (Result: **$Authorization**)**
3. **Retrieve related **SelectedDrive** via Association from **$Explorer** (Result: **$Drive**)**
4. **Call Microflow **Sharepoint.GetDriveItems** (Result: **$Result**)**
5. **Create **Sharepoint.Children** (Result: **$NewChildren**)
      - Set **Parent** = `$DriveItem`**
6. 🔄 **LOOP:** For each **$IteratorDriveItem** in **$Result**
   │ 1. **Update **$IteratorDriveItem**
      - Set **ListedDriveItems** = `$Explorer`
      - Set **Child** = `$NewChildren`**
   └─ **End Loop**
7. 🏁 **END:** Return `$Result`

**Final Result:** This process concludes by returning a [List] value.