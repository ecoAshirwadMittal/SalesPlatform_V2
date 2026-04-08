# Microflow Analysis: SUB_OrderDetails_Export_ByDevice

### Requirements (Inputs):
- **$OfferAndOrdersView** (A record of type: EcoATM_PWS.OfferAndOrdersView)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Create Object
      - Store the result in a new variable called **$NewOrderDetailsExportByDevice****
4. **Search the Database for **EcoATM_PWS.IMEIDetail** using filter: { [EcoATM_PWS.IMEIDetail_OfferItem/EcoATM_PWS.OfferItem/EcoATM_PWS.OfferItem_Order/EcoATM_PWS.Order/OrderNumber=$OfferAndOrdersView/OrderNumber] } (Call this list **$IMEIDetailsList**)**
5. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name = 'PWSOrderDetailsByDevice']
 } (Call this list **$MxTemplate**)**
6. **Create Variable**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Permanently save **$undefined** to the database.**
9. **Create Variable**
10. **Run another process: "EcoATM_PWS.SUB_OrderDetailsByDevice_GenerateReport"** ⚠️ *(This step has a safety catch if it fails)*
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
