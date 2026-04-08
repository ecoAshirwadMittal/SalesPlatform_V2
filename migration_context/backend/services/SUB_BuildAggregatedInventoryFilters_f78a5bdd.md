# Microflow Analysis: SUB_BuildAggregatedInventoryFilters

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
5. **Search the Database for **EcoATM_MDM.Brand** using filter: { Show everything } (Call this list **$BrandList**)**
6. **Search the Database for **EcoATM_MDM.Carrier** using filter: { Show everything } (Call this list **$CarrierList**)**
7. **Search the Database for **EcoATM_MDM.Model** using filter: { Show everything } (Call this list **$ModelList**)**
8. **Search the Database for **EcoATM_MDM.ModelName** using filter: { Show everything } (Call this list **$ModelNameList**)**
9. **Run another process: "AuctionUI.SUB_ResetAuctionFilters"**
10. **Run another process: "AuctionUI.SUB_CreateBrandList"**
11. **Run another process: "AuctionUI.SUB_CreateCarrierList"**
12. **Run another process: "AuctionUI.SUB_CreateModelList"**
13. **Run another process: "AuctionUI.SUB_CreateModelNameList"**
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Run another process: "AuctionUI.SUB_CreateBrandList"**
16. **Run another process: "AuctionUI.SUB_CreateCarrierList"**
17. **Run another process: "AuctionUI.SUB_CreateModelList"**
18. **Run another process: "AuctionUI.SUB_CreateModelNameList"**
19. **Take the list **$AggregatedInventoryList**, perform a [FilterByExpression] where: { $currentObject/DWTotalQuantity > 0 }, and call the result **$AggregatedInventoryList_DW****
20. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
21. **Permanently save **$undefined** to the database.**
22. **Permanently save **$undefined** to the database.**
23. **Permanently save **$undefined** to the database.**
24. **Permanently save **$undefined** to the database.**
25. **Permanently save **$undefined** to the database.**
26. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
27. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
