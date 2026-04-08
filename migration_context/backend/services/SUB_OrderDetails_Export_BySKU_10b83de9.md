# Microflow Analysis: SUB_OrderDetails_Export_BySKU

### Requirements (Inputs):
- **$OfferAndOrdersView** (A record of type: EcoATM_PWS.OfferAndOrdersView)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Create Object
      - Store the result in a new variable called **$NewOrderDetailsExport****
4. **Run another process: "EcoATM_PWS.DS_OrderHistoryDetails"
      - Store the result in a new variable called **$OfferItemList****
5. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name = 'PWSOrderDetails']
 } (Call this list **$MxTemplate**)**
6. **Create Variable**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Permanently save **$undefined** to the database.**
9. **Create Variable**
10. **Run another process: "EcoATM_PWS.SUB_OrderDetails_GenerateReport"** ⚠️ *(This step has a safety catch if it fails)*
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
