# Microflow Analysis: ACT_ImportTemplateDocument

### Requirements (Inputs):
- **$TemplateDocument** (A record of type: ExcelImporter.TemplateDocument)
- **$InventoryUploadHelper** (A record of type: AuctionUI.InventoryUploadHelper)
- **$Week** (A record of type: AuctionUI.Week)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Template****
2. **Decision:** "has a template selected"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Log Message**
4. **Java Action Call
      - Store the result in a new variable called **$rowCount**** ⚠️ *(This step has a safety catch if it fails)*
5. **Log Message**
6. **Update the **$undefined** (Object):
      - Change [AuctionUI.InventoryUploadHelper.TotalRows] to: "$rowCount"
      - **Save:** This change will be saved to the database immediately.**
7. **Show Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
