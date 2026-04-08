# Microflow Analysis: SUB_Oracle_ErrorMessage

### Requirements (Inputs):
- **$DataContent** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **EcoATM_PWSIntegration.PWSResponseConfig** using filter: { Show everything } (Call this list **$OriginalPWSResponseConfigList**)**
2. **Import Xml** ⚠️ *(This step has a safety catch if it fails)*
3. **Create List
      - Store the result in a new variable called **$DeletePWSResponseConfigList****
4. **Create List
      - Store the result in a new variable called **$SavePWSResponseConfigList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Delete**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
