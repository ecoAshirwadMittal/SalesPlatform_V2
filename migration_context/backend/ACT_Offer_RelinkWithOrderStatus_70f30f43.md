# Microflow Analysis: ACT_Offer_RelinkWithOrderStatus

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_PWS.Offer** using filter: { 
[OfferStatus!=empty]
[not(EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus)]
 } (Call this list **$OfferList**)**
3. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create List
      - Store the result in a new variable called **$OfferToCommitList****
5. **Search the Database for **EcoATM_PWS.OrderStatus** using filter: { Show everything } (Call this list **$OrderStatusList**)**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Permanently save **$undefined** to the database.**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Show Message**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
