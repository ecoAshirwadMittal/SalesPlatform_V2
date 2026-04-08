# Microflow Detailed Specification: ACT_DeleteInventoryData

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_PWSMDM.Device**  (Result: **$DeviceList**)**
3. **DB Retrieve **EcoATM_PWSMDM.Brand**  (Result: **$BrandList**)**
4. **DB Retrieve **EcoATM_PWSMDM.Capacity**  (Result: **$CapacityList**)**
5. **DB Retrieve **EcoATM_PWSMDM.Carrier**  (Result: **$CarrierList**)**
6. **DB Retrieve **EcoATM_PWSMDM.Category**  (Result: **$CategoryList**)**
7. **DB Retrieve **EcoATM_PWSMDM.Color**  (Result: **$ColorList**)**
8. **DB Retrieve **EcoATM_PWSMDM.Condition**  (Result: **$ConditionList**)**
9. **DB Retrieve **EcoATM_PWSMDM.Grade**  (Result: **$GradeList**)**
10. **DB Retrieve **EcoATM_PWSMDM.Model**  (Result: **$ModelList**)**
11. **Delete**
12. **Delete**
13. **Delete**
14. **Delete**
15. **Delete**
16. **Delete**
17. **Delete**
18. **Delete**
19. **Delete**
20. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
21. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.