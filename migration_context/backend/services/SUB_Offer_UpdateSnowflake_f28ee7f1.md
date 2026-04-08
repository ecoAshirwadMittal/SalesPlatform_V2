# Microflow Analysis: SUB_Offer_UpdateSnowflake

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)
- **$updatedBy** (A record of type: Object)
- **$EventString** (A record of type: Object)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
3. **Decision:** "SubmitOfferToSnowflak flag?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Run another process: "EcoATM_PWS.SUB_Offer_ConvertToJSON"
      - Store the result in a new variable called **$JSONContent****
5. **Decision:** "JSON content?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Create Variable**
7. **Create List
      - Store the result in a new variable called **$StoreProcedureArgumentList****
8. **Create Object
      - Store the result in a new variable called **$ArgumentJSON_CONTENT****
9. **Change List**
10. **Create Object
      - Store the result in a new variable called **$ArgumentACTING_USER****
11. **Change List**
12. **Create Variable**
13. **Run another process: "Custom_Logging.SUB_Log_Info"**
14. **Java Action Call
      - Store the result in a new variable called **$IsSuccess****
15. **Decision:** "IsSuccess"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
16. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
17. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
