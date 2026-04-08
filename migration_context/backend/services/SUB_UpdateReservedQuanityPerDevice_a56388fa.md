# Microflow Analysis: SUB_UpdateReservedQuanityPerDevice

### Requirements (Inputs):
- **$DeviceList** (A record of type: EcoATM_PWSMDM.Device)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create List
      - Store the result in a new variable called **$OfferItemList_Filtered****
3. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [Reserved]
[EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus='Ordered']
[EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='PWS']
 } (Call this list **$OfferItemList**)**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Permanently save **$undefined** to the database.**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Permanently save **$undefined** to the database.**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
