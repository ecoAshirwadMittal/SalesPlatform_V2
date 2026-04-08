# Microflow Analysis: SUB_CreateCarrierList

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)
- **$CarrierList** (A record of type: EcoATM_MDM.Carrier)
- **$enum_BuyerCodeType** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Create Variable**
5. **Decision:** "?type"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Change Variable**
7. **Java Action Call
      - Store the result in a new variable called **$CarrierHelperList**** ⚠️ *(This step has a safety catch if it fails)*
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
