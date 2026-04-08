# Microflow Analysis: SUB_ImeiData_UpdateSnowflake

### Requirements (Inputs):
- **$Order** (A record of type: EcoATM_PWS.Order)
- **$updatedBy** (A record of type: Object)
- **$EventString** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.IMEIData** using filter: { [OrderNum=$Order/OrderNumber]
 } (Call this list **$IMEIDataList**)**
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
4. **Decision:** "SubmitOfferToSnowflak flag?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Export Xml**
6. **Decision:** "JSON content?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Create Variable**
8. **Create List
      - Store the result in a new variable called **$StoreProcedureArgumentList****
9. **Create Object
      - Store the result in a new variable called **$ArgumentJSON_CONTENT****
10. **Change List**
11. **Create Object
      - Store the result in a new variable called **$ArgumentACTING_USER****
12. **Change List**
13. **Create Variable**
14. **Run another process: "Custom_Logging.SUB_Log_Info"**
15. **Java Action Call
      - Store the result in a new variable called **$IsSuccess****
16. **Decision:** "IsSuccess"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
17. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
18. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
