# Microflow Analysis: SUB_LoadPWSInventory_Task

### Requirements (Inputs):
- **$Limit** (A record of type: Object)
- **$Offset** (A record of type: Object)
- **$MDMFuturePriceHelper** (A record of type: EcoATM_PWS.MDMFuturePriceHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Execute Database Query
      - Store the result in a new variable called **$VW_FACT_INVENTORY_PWS_CURRENT_1**** ⚠️ *(This step has a safety catch if it fails)*
3. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
4. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
5. **Take the list **$VW_FACT_INVENTORY_PWS_CURRENT_1**, perform a [Head], and call the result **$VW_FACT_INVENTORY_PWS_CURRENT****
6. **Create Variable**
7. **Import Xml** ⚠️ *(This step has a safety catch if it fails)*
8. **Aggregate List
      - Store the result in a new variable called **$DeviceCount****
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
