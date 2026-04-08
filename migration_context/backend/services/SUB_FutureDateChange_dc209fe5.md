# Microflow Analysis: SUB_FutureDateChange

### Requirements (Inputs):
- **$MDMFuturePriceHelper** (A record of type: EcoATM_PWS.MDMFuturePriceHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Decision:** "Date Valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Retrieve
      - Store the result in a new variable called **$DeviceList_FuturePrice****
4. **Create List
      - Store the result in a new variable called **$NewPriceList_FuturePrice****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { [not(EcoATM_PWSMDM.Price_Device_Future/EcoATM_PWSMDM.PriceHistory)]
 } (Call this list **$DeviceList_NoFuturePrice**)**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Permanently save **$undefined** to the database.**
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
