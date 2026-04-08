# Microflow Analysis: SUB_Offer_UpdateSnowflake

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)
- **$updatedBy** (A record of type: Object)
- **$EventString** (A record of type: Object)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "EcoATM_PWS.SUB_Offer_ConvertToJSON"
      - Store the result in a new variable called **$JSONContent****
3. **Decision:** "JSON content?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create Variable**
5. **Create Variable**
6. **Java Action Call
      - Store the result in a new variable called **$Variable****
7. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName**** ⚠️ *(This step has a safety catch if it fails)*
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
