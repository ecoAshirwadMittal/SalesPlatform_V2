# Microflow Analysis: SUB_DisableInactiveDevices

### Requirements (Inputs):
- **$DeviceList_Inactive** (A record of type: EcoATM_PWSMDM.Device)

### Execution Steps:
1. **Create List
      - Store the result in a new variable called **$BuyerOfferItemList****
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Aggregate List
      - Store the result in a new variable called **$InactiveDevicesCount****
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Permanently save **$undefined** to the database.**
6. **Aggregate List
      - Store the result in a new variable called **$RemoveBuyerItemsCount****
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Delete**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
