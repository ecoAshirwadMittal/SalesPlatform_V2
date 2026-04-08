# Microflow Detailed Specification: ACT_WeeklyPODELETE

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PO.PODetail** Filter: `[ProductID=16687 and EcoATM_PO.PODetail_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code='ADPO']` (Result: **$PODetailList_2**)**
2. **DB Retrieve **EcoATM_PO.PODetail** Filter: `[ProductID=16687 and EcoATM_PO.PODetail_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code='ADPO']` (Result: **$PODetailList_2_1**)**
3. **Retrieve related **WeeklyPO_PODetail** via Association from **$PODetailList_2** (Result: **$WeeklyPOList**)**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.