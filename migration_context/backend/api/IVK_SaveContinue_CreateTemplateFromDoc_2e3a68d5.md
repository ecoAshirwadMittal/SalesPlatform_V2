# Microflow Analysis: IVK_SaveContinue_CreateTemplateFromDoc

### Requirements (Inputs):
- **$TemplateDocument** (A record of type: ExcelImporter.TemplateDocument)

### Execution Steps:
1. **Run another process: "ExcelImporter.Validate_TemplateDocument"
      - Store the result in a new variable called **$isValid****
2. **Decision:** "validate template document"
   - If [true] -> Move to: **Retrieve the selected template**
   - If [false] -> Move to: **Finish**
3. **Retrieve
      - Store the result in a new variable called **$Template****
4. **Run another process: "ExcelImporter.SF_Template_CheckNrs"
      - Store the result in a new variable called **$IsValid****
5. **Decision:** "Is valid template?"
   - If [true] -> Move to: **Commit the tempalte**
   - If [false] -> Move to: **Finish**
6. **Update the **$undefined** (Object):
      - **Save:** This change will be saved to the database immediately.**
7. **Java Action Call
      - Store the result in a new variable called **$nothing****
8. **Delete**
9. **Close Form**
10. **Show Page**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
