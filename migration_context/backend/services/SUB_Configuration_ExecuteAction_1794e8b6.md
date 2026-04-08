# Microflow Analysis: SUB_Configuration_ExecuteAction

### Requirements (Inputs):
- **$Environment** (A record of type: Custom_Logging.Environment)
- **$ConfigurationList** (A record of type: Custom_Logging.Configuration)

### Execution Steps:
1. **Decision:** "Config?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Create List
      - Store the result in a new variable called **$ConfigurationByFileList****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Permanently save **$undefined** to the database.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
