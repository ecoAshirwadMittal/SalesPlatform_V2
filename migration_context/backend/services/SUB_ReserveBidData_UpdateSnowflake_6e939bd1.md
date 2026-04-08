# Microflow Analysis: SUB_ReserveBidData_UpdateSnowflake

### Requirements (Inputs):
- **$JSONContent** (A record of type: Object)
- **$updatedBy** (A record of type: Object)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
3. **Decision:** "SubmitOfferToSnowflak flag?"
   - If [true] -> Move to: **JSON content?**
   - If [false] -> Move to: **Activity**
4. **Decision:** "JSON content?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Create Variable**
6. **Create List
      - Store the result in a new variable called **$StoreProcedureArgumentList****
7. **Create Object
      - Store the result in a new variable called **$ArgumentJSON_CONTENT****
8. **Change List**
9. **Create Object
      - Store the result in a new variable called **$ArgumentACTING_USER****
10. **Change List**
11. **Create Variable**
12. **Run another process: "Custom_Logging.SUB_Log_Info"**
13. **Java Action Call
      - Store the result in a new variable called **$IsSuccess****
14. **Decision:** "IsSuccess"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
15. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
16. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
