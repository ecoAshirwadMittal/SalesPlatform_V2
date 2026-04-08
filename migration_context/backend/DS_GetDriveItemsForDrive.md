# Microflow Detailed Specification: DS_GetDriveItemsForDrive

### 📥 Inputs (Parameters)
- **$Drive** (Type: Sharepoint.Drive)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SelectedDrive** via Association from **$Drive** (Result: **$Explorer**)**
2. **Retrieve related **Explorer_Authorization** via Association from **$Explorer** (Result: **$Authorization**)**
3. **Call Microflow **Sharepoint.GetDriveItems** (Result: **$DriveItemList**)**
4. 🔄 **LOOP:** For each **$IteratorDriveItem** in **$DriveItemList**
   │ 1. **Update **$IteratorDriveItem**
      - Set **ListedDriveItems** = `$Explorer`**
   └─ **End Loop**
5. 🏁 **END:** Return `$DriveItemList`

**Final Result:** This process concludes by returning a [List] value.