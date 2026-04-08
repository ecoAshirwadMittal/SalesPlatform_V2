# Microflow Detailed Specification: ACT_DownloadItem

### 📥 Inputs (Parameters)
- **$ListItem** (Type: Sharepoint.ListItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **ListItem_Explorer** via Association from **$ListItem** (Result: **$Explorer**)**
2. **Retrieve related **Explorer_Authorization** via Association from **$Explorer** (Result: **$Authorization**)**
3. **Retrieve related **SelectedSite** via Association from **$Explorer** (Result: **$Site**)**
4. **Retrieve related **SelectedList** via Association from **$Explorer** (Result: **$List**)**
5. **Call Microflow **Sharepoint.DownloadDriveItem** (Result: **$Item**)**
6. **Update **$Item** (and Save to DB)
      - Set **DeleteAfterDownload** = `true`**
7. **DownloadFile**
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.