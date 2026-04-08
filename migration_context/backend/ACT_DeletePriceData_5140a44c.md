# Microflow Analysis: ACT_DeletePriceData

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_PWSMDM.PriceHistory** using filter: { Show everything } (Call this list **$PriceList**)**
3. **Delete**
4. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
