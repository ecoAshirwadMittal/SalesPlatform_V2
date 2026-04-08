# Microflow Analysis: IVK_Column_Save

### Requirements (Inputs):
- **$Column** (A record of type: ExcelImporter.Column)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Datasource"
   - If [CellValue] -> Move to: **Finish**
   - If [(empty)] -> Move to: **Show required msg**
   - If [DocumentPropertyRowNr] -> Move to: **Finish**
   - If [DocumentPropertySheetNr] -> Move to: **Finish**
   - If [StaticValue] -> Move to: **Finish**
3. **Decision:** "has col number?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Show required msg**
4. **Decision:** "has text?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Show required msg**
5. **Decision:** "mapping type"
   - If [(empty)] -> Move to: **Show required msg**
   - If [Attribute] -> Move to: **has key?**
   - If [Reference] -> Move to: **has ref key?**
   - If [DoNotUse] -> Move to: **Finish**
6. **Validation Feedback**
7. **Change Variable**
8. **Decision:** "valid?"
   - If [true] -> Move to: **Call before commit**
   - If [false] -> Move to: **Finish**
9. **Run another process: "ExcelImporter.BCo_Column"
      - Store the result in a new variable called **$commitResult****
10. **Decision:** "valid?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Save the column object and refresh**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
