# Microflow Detailed Specification: SUB_CleanupMetaData

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSMDM.Brand** Filter: `[not(EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Device)]` (Result: **$BrandList**)**
2. **DB Retrieve **EcoATM_PWSMDM.Capacity** Filter: `[not(EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Device)]` (Result: **$CapacityList**)**
3. **DB Retrieve **EcoATM_PWSMDM.Carrier** Filter: `[not(EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Device)]` (Result: **$CarrierList**)**
4. **DB Retrieve **EcoATM_PWSMDM.Category** Filter: `[not(EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Device)]` (Result: **$CategoryList**)**
5. **DB Retrieve **EcoATM_PWSMDM.Color** Filter: `[not(EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Device)]` (Result: **$ColorList**)**
6. **DB Retrieve **EcoATM_PWSMDM.Condition** Filter: `[not(EcoATM_PWSMDM.Device_Condition/EcoATM_PWSMDM.Device)]` (Result: **$ConditionList**)**
7. **DB Retrieve **EcoATM_PWSMDM.Grade** Filter: `[not(EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Device)]` (Result: **$GradeList**)**
8. **DB Retrieve **EcoATM_PWSMDM.Model** Filter: `[not(EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Device)]` (Result: **$ModelList**)**
9. **DB Retrieve **EcoATM_PWSMDM.Note** Filter: `[not(EcoATM_PWSMDM.Device_Note/EcoATM_PWSMDM.Device)]` (Result: **$NoteList**)**
10. **Delete**
11. **Delete**
12. **Delete**
13. **Delete**
14. **Delete**
15. **Delete**
16. **Delete**
17. **Delete**
18. **Delete**
19. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.