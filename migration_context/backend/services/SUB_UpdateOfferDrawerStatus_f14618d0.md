# Microflow Analysis: SUB_UpdateOfferDrawerStatus

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create List
      - Store the result in a new variable called **$DeviceList****
3. **Create List
      - Store the result in a new variable called **$CaseLotList****
4. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
5. **Decision:** "OfferStatus?"
   - If [Sales_Review] -> Move to: **Finish**
   - If [Buyer_Acceptance] -> Move to: **Finish**
   - If [Ordered] -> Move to: **Finish**
   - If [Declined] -> Move to: **Finish**
   - If [Pending_Order] -> Move to: **Finish**
   - If [(empty)] -> Move to: **Activity**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Permanently save **$undefined** to the database.**
8. **Permanently save **$undefined** to the database.**
9. **Permanently save **$undefined** to the database.**
10. **Search the Database for **Administration.Account** using filter: { [id=$currentUser] } (Call this list **$Account**)**
11. **Run another process: "EcoATM_PWS.SUB_Offer_UpdateSnowflake"**
12. **Run another process: "EcoATM_PWS.SUB_SetAndResetTags_PerOffer"**
13. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
