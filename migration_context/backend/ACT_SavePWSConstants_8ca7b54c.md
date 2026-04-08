# Microflow Analysis: ACT_SavePWSConstants

### Requirements (Inputs):
- **$PWSConstants** (A record of type: EcoATM_PWS.PWSConstants)

### Execution Steps:
1. **Run another process: "EcoATM_PWS.VAL_PWSConstants"
      - Store the result in a new variable called **$isValid****
2. **Decision:** "valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Permanently save **$undefined** to the database.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
