# Microflow Analysis: SUB_CleanupMetaData

### Execution Steps:
1. **Search the Database for **EcoATM_PWSMDM.Brand** using filter: { [not(EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Device)]
 } (Call this list **$BrandList**)**
2. **Search the Database for **EcoATM_PWSMDM.Capacity** using filter: { [not(EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Device)]
 } (Call this list **$CapacityList**)**
3. **Search the Database for **EcoATM_PWSMDM.Carrier** using filter: { [not(EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Device)]
 } (Call this list **$CarrierList**)**
4. **Search the Database for **EcoATM_PWSMDM.Category** using filter: { [not(EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Device)]
 } (Call this list **$CategoryList**)**
5. **Search the Database for **EcoATM_PWSMDM.Color** using filter: { [not(EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Device)]
 } (Call this list **$ColorList**)**
6. **Search the Database for **EcoATM_PWSMDM.Condition** using filter: { [not(EcoATM_PWSMDM.Device_Condition/EcoATM_PWSMDM.Device)]
 } (Call this list **$ConditionList**)**
7. **Search the Database for **EcoATM_PWSMDM.Grade** using filter: { [not(EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Device)]
 } (Call this list **$GradeList**)**
8. **Search the Database for **EcoATM_PWSMDM.Model** using filter: { [not(EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Device)]
 } (Call this list **$ModelList**)**
9. **Search the Database for **EcoATM_PWSMDM.Note** using filter: { [not(EcoATM_PWSMDM.Device_Note/EcoATM_PWSMDM.Device)]
 } (Call this list **$NoteList**)**
10. **Delete**
11. **Delete**
12. **Delete**
13. **Delete**
14. **Delete**
15. **Delete**
16. **Delete**
17. **Delete**
18. **Delete**
19. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
