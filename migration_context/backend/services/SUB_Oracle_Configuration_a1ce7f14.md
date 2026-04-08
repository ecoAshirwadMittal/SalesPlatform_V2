# Microflow Analysis: SUB_Oracle_Configuration

### Requirements (Inputs):
- **$DataContent** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **EcoATM_PWSIntegration.PWSConfiguration** using filter: { Show everything } (Call this list **$PWSConfigurationList**)**
2. **Import Xml** ⚠️ *(This step has a safety catch if it fails)*
3. **Delete**
4. **Permanently save **$undefined** to the database.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
