# Microflow Analysis: ACT_DeleteInventoryData

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { Show everything } (Call this list **$DeviceList**)**
3. **Search the Database for **EcoATM_PWSMDM.Brand** using filter: { Show everything } (Call this list **$BrandList**)**
4. **Search the Database for **EcoATM_PWSMDM.Capacity** using filter: { Show everything } (Call this list **$CapacityList**)**
5. **Search the Database for **EcoATM_PWSMDM.Carrier** using filter: { Show everything } (Call this list **$CarrierList**)**
6. **Search the Database for **EcoATM_PWSMDM.Category** using filter: { Show everything } (Call this list **$CategoryList**)**
7. **Search the Database for **EcoATM_PWSMDM.Color** using filter: { Show everything } (Call this list **$ColorList**)**
8. **Search the Database for **EcoATM_PWSMDM.Condition** using filter: { Show everything } (Call this list **$ConditionList**)**
9. **Search the Database for **EcoATM_PWSMDM.Grade** using filter: { Show everything } (Call this list **$GradeList**)**
10. **Search the Database for **EcoATM_PWSMDM.Model** using filter: { Show everything } (Call this list **$ModelList**)**
11. **Delete**
12. **Delete**
13. **Delete**
14. **Delete**
15. **Delete**
16. **Delete**
17. **Delete**
18. **Delete**
19. **Delete**
20. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
21. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
