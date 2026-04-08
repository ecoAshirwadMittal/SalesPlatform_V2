# Microflow Analysis: IVK_SaveTemplate

### Requirements (Inputs):
- **$Template** (A record of type: ExcelImporter.Template)

### Execution Steps:
1. **Run another process: "ExcelImporter.CleanupOldRefHandling"
      - Store the result in a new variable called **$Variable****
2. **Run another process: "ExcelImporter.ValidateTemplate"
      - Store the result in a new variable called **$valid****
3. **Decision:** "valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Set the template invalid**
4. **Run another process: "ExcelImporter.GetAddProperties"
      - Store the result in a new variable called **$AddProperties****
5. **Update the **$undefined** (Object):
      - **Save:** This change will be saved to the database immediately.**
6. **Update the **$undefined** (Object):
      - Change [ExcelImporter.Template.Status] to: "ExcelImporter.Status.VALID"
      - **Save:** This change will be saved to the database immediately.**
7. **Close Form**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
