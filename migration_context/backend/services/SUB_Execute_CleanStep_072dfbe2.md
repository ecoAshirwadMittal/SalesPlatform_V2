# Microflow Analysis: SUB_Execute_CleanStep

### Requirements (Inputs):
- **$query** (A record of type: Object)
- **$StepInfo** (A record of type: Object)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Java Action Call
      - Store the result in a new variable called **$IsSuccess**** ⚠️ *(This step has a safety catch if it fails)*
3. **Decision:** "IsSuccess?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
