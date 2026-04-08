# Microflow Analysis: ACT_FixAllSalesRepNames

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Search the Database for **EcoATM_PWS.Offer** using filter: { [not(EcoATM_PWS.Offer_SalesRepresentative/EcoATM_BuyerManagement.SalesRepresentative)] } (Call this list **$OfferList**)**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
