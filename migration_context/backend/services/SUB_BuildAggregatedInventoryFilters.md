# Microflow Detailed Specification: SUB_BuildAggregatedInventoryFilters

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `'Build Aggregated Inventory Filters'`**
2. **Create Variable **$TimerName** = `'BuildAggregatedInventoryFilters'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
5. **DB Retrieve **EcoATM_MDM.Brand**  (Result: **$BrandList**)**
6. **DB Retrieve **EcoATM_MDM.Carrier**  (Result: **$CarrierList**)**
7. **DB Retrieve **EcoATM_MDM.Model**  (Result: **$ModelList**)**
8. **DB Retrieve **EcoATM_MDM.ModelName**  (Result: **$ModelNameList**)**
9. **Call Microflow **AuctionUI.SUB_ResetAuctionFilters****
10. **Call Microflow **AuctionUI.SUB_CreateBrandList****
11. **Call Microflow **AuctionUI.SUB_CreateCarrierList****
12. **Call Microflow **AuctionUI.SUB_CreateModelList****
13. **Call Microflow **AuctionUI.SUB_CreateModelNameList****
14. 🔄 **LOOP:** For each **$IteratorAggregatedInventory** in **$AggregatedInventoryList**
   │ 1. **Call Microflow **AuctionUI.SUB_UpdateAggegatedInventoryFilterAssociations_Brand****
   │ 2. **Call Microflow **AuctionUI.SUB_UpdateAggegatedInventoryFilterAssociations_Carrier****
   │ 3. **Call Microflow **AuctionUI.SUB_UpdateAggegatedInventoryFilterAssociations_Model****
   │ 4. **Call Microflow **AuctionUI.SUB_UpdateAggegatedInventoryFilterAssociations_ModelName****
   └─ **End Loop**
15. **Call Microflow **AuctionUI.SUB_CreateBrandList****
16. **Call Microflow **AuctionUI.SUB_CreateCarrierList****
17. **Call Microflow **AuctionUI.SUB_CreateModelList****
18. **Call Microflow **AuctionUI.SUB_CreateModelNameList****
19. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/DWTotalQuantity > 0` (Result: **$AggregatedInventoryList_DW**)**
20. 🔄 **LOOP:** For each **$IteratorAggregatedInventory_DW** in **$AggregatedInventoryList_DW**
   │ 1. **Call Microflow **AuctionUI.SUB_UpdateAggegatedInventoryFilterAssociations_Brand****
   │ 2. **Call Microflow **AuctionUI.SUB_UpdateAggegatedInventoryFilterAssociations_Carrier****
   │ 3. **Call Microflow **AuctionUI.SUB_UpdateAggegatedInventoryFilterAssociations_Model****
   │ 4. **Call Microflow **AuctionUI.SUB_UpdateAggegatedInventoryFilterAssociations_ModelName****
   └─ **End Loop**
21. **Commit/Save **$BrandList** to Database**
22. **Commit/Save **$CarrierList** to Database**
23. **Commit/Save **$ModelList** to Database**
24. **Commit/Save **$ModelNameList** to Database**
25. **Commit/Save **$AggregatedInventoryList** to Database**
26. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
27. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.