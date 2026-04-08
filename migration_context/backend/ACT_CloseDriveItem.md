# Microflow Detailed Specification: ACT_CloseDriveItem

### 📥 Inputs (Parameters)
- **$DriveItem** (Type: Sharepoint.DriveItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Parent** via Association from **$DriveItem** (Result: **$Children**)**
2. **Delete**
3. **Close current page/popup**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.