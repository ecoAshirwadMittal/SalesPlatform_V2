# Microflow Analysis: SUB_ResetAuctionFilters

### Requirements (Inputs):
- **$BrandList** (A record of type: EcoATM_MDM.Brand)
- **$CarrierList** (A record of type: EcoATM_MDM.Carrier)
- **$ModelList** (A record of type: EcoATM_MDM.Model)
- **$ModelNameList** (A record of type: EcoATM_MDM.ModelName)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
