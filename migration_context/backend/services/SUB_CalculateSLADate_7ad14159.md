# Microflow Analysis: SUB_CalculateSLADate

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Search the Database for **EcoATM_PWS.PWSConstants** using filter: { Show everything } (Call this list **$PWSFeatureConfig**)**
3. **Search the Database for **EcoATM_MDM.CompanyHoliday** using filter: { Show everything } (Call this list **$CompanyHolidayList**)**
4. **Create Variable**
5. **Create Variable**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [DateTime] result.
