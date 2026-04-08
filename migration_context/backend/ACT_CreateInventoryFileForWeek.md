# Microflow Detailed Specification: ACT_CreateInventoryFileForWeek

### 📥 Inputs (Parameters)
- **$Week** (Type: AuctionUI.Week)
- **$InventoryUploadHelper** (Type: AuctionUI.InventoryUploadHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TemplateName** = `'Auction Inventory Template'`**
2. **DB Retrieve **ExcelImporter.Template** Filter: `[(Title = $TemplateName)]` (Result: **$TemplateDocument**)**
3. **Create **AuctionUI.InventoryDoc** (Result: **$NewInventoryDoc**)
      - Set **Week_InventoryDoc** = `$Week`
      - Set **Name** = `$Week/AuctionUI.Week_InventoryDoc/AuctionUI.InventoryDoc/Name`
      - Set **DeleteAfterDownload** = `false`
      - Set **Size** = `$Week/AuctionUI.Week_InventoryDoc/AuctionUI.InventoryDoc/Size`
      - Set **TemplateDocument_Template** = `$TemplateDocument`**
4. **Update **$InventoryUploadHelper** (and Save to DB)
      - Set **InventoryUploadHelper_InventoryDoc** = `$NewInventoryDoc`**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.