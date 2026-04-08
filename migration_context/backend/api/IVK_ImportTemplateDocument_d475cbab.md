# Microflow Analysis: IVK_ImportTemplateDocument

### Requirements (Inputs):
- **$TemplateDocument** (A record of type: ExcelImporter.TemplateDocument)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Template****
2. **Decision:** "has a template selected"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Java Action Call
      - Store the result in a new variable called **$rowCount**** ⚠️ *(This step has a safety catch if it fails)*
4. **Show Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
