# Microflow Analysis: SUB_ConfigurationFile_ImportData

### Requirements (Inputs):
- **$FileToImport** (A record of type: Object)
- **$MicroflowToExecute** (A record of type: Object)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Java Action Call
      - Store the result in a new variable called **$FileContent**** ⚠️ *(This step has a safety catch if it fails)*
3. **Java Action Call
      - Store the result in a new variable called **$ResultMessage**** ⚠️ *(This step has a safety catch if it fails)*
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
