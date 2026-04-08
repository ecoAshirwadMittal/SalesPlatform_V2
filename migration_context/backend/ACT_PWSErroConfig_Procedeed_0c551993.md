# Microflow Analysis: ACT_PWSErroConfig_Procedeed

### Requirements (Inputs):
- **$ManageFileDocument** (A record of type: EcoATM_PWS.ManageFileDocument)

### Execution Steps:
1. **Decision:** "has content?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Java Action Call
      - Store the result in a new variable called **$JSONFileContent****
3. **Run another process: "EcoATM_PWSIntegration.SUB_Oracle_ErrorMessage"
      - Store the result in a new variable called **$IsSuccess****
4. **Close Form**
5. **Create Object
      - Store the result in a new variable called **$NewUserMessage****
6. **Show Page**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
