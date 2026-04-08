# Microflow Detailed Specification: ACT_ImportTemplateDocument

### 📥 Inputs (Parameters)
- **$TemplateDocument** (Type: ExcelImporter.TemplateDocument)
- **$InventoryUploadHelper** (Type: AuctionUI.InventoryUploadHelper)
- **$Week** (Type: AuctionUI.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **TemplateDocument_Template** via Association from **$TemplateDocument** (Result: **$Template**)**
2. 🔀 **DECISION:** `$Template != empty`
   ➔ **If [true]:**
      1. **LogMessage**
      2. **JavaCallAction**
      3. **LogMessage**
      4. **Update **$InventoryUploadHelper** (and Save to DB)
      - Set **TotalRows** = `$rowCount`**
      5. **Show Message (Information): `The import is finished. {1} records have been imported.`**
      6. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **Update **$InventoryUploadHelper**
      - Set **FileImportComplete** = `true`
      - Set **Message** = `'No template selected for this file'`**
      2. **Show Message (Warning): `No template selected for this file`**
      3. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.