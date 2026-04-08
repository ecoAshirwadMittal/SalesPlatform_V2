# Microflow Analysis: ACT_CreateInventoryFileForWeek

### Requirements (Inputs):
- **$Week** (A record of type: AuctionUI.Week)
- **$InventoryUploadHelper** (A record of type: AuctionUI.InventoryUploadHelper)

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **ExcelImporter.Template** using filter: { [(Title = $TemplateName)] } (Call this list **$TemplateDocument**)**
3. **Create Object
      - Store the result in a new variable called **$NewInventoryDoc****
4. **Update the **$undefined** (Object):
      - Change [AuctionUI.InventoryUploadHelper_InventoryDoc] to: "$NewInventoryDoc"
      - **Save:** This change will be saved to the database immediately.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
