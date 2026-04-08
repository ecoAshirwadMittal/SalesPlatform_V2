# Microflow Analysis: ACT_ExecuteOQL

### Requirements (Inputs):
- **$Query** (A record of type: OQL.Query)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$CSVDownload**** ⚠️ *(This step has a safety catch if it fails)*
2. **Decision:** "As download?"
   - If [DOWNLOAD] -> Move to: **Activity**
   - If [TABLE] -> Move to: **Activity**
   - If [(empty)] -> Move to: **Finish**
3. **Update the **$undefined** (Object):
      - Change [System.FileDocument.DeleteAfterDownload] to: "true"
      - **Save:** This change will be saved to the database immediately.**
4. **Download File**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
