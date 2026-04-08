# Microflow Detailed Specification: SUB_ProcessAvailableQuantityForFilters

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSMDM.Category** Filter: `[EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Device [ ATPQty> 0 and IsActive ] ]` (Result: **$Category_AvlQtyList**)**
2. 🔄 **LOOP:** For each **$IteratorCategory** in **$Category_AvlQtyList**
   │ 1. **Update **$IteratorCategory**
      - Set **IsEnabledForFilter** = `trim($IteratorCategory/Category) != ''`**
   └─ **End Loop**
3. **DB Retrieve **EcoATM_PWSMDM.Brand** Filter: `[EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Device [ ATPQty> 0 and IsActive ] ]` (Result: **$Brand_AvlQtyList**)**
4. 🔄 **LOOP:** For each **$IteratorBrand** in **$Brand_AvlQtyList**
   │ 1. **Update **$IteratorBrand**
      - Set **IsEnabledForFilter** = `trim($IteratorBrand/Brand) != ''`**
   └─ **End Loop**
5. **DB Retrieve **EcoATM_PWSMDM.Carrier** Filter: `[EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Device [ ATPQty> 0 and IsActive ] ]` (Result: **$Carrier_AvlQtyList**)**
6. 🔄 **LOOP:** For each **$IteratorCarrier** in **$Carrier_AvlQtyList**
   │ 1. **Update **$IteratorCarrier**
      - Set **IsEnabledForFilter** = `trim($IteratorCarrier/Carrier) != ''`**
   └─ **End Loop**
7. **DB Retrieve **EcoATM_PWSMDM.Capacity** Filter: `[EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Device [ ATPQty> 0 and IsActive ] ]` (Result: **$Capacity_AvlQtyList**)**
8. 🔄 **LOOP:** For each **$IteratorCapacity** in **$Capacity_AvlQtyList**
   │ 1. **Update **$IteratorCapacity**
      - Set **IsEnabledForFilter** = `trim($IteratorCapacity/Capacity) != ''`**
   └─ **End Loop**
9. **DB Retrieve **EcoATM_PWSMDM.Color** Filter: `[EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Device [ ATPQty> 0 and IsActive ] ]` (Result: **$Color_AvlQtyList**)**
10. 🔄 **LOOP:** For each **$IteratorColor** in **$Color_AvlQtyList**
   │ 1. **Update **$IteratorColor**
      - Set **IsEnabledForFilter** = `trim($IteratorColor/Color) != ''`**
   └─ **End Loop**
11. **DB Retrieve **EcoATM_PWSMDM.Grade** Filter: `[EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Device [ ATPQty> 0 and IsActive ] ]` (Result: **$Grade_AvlQtyList**)**
12. 🔄 **LOOP:** For each **$IteratorGrade** in **$Grade_AvlQtyList**
   │ 1. **Update **$IteratorGrade**
      - Set **IsEnabledForFilter** = `trim($IteratorGrade/Grade) != ''`**
   └─ **End Loop**
13. **DB Retrieve **EcoATM_PWSMDM.Model** Filter: `[EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Device [ ATPQty> 0 and IsActive ] ]` (Result: **$Model_AvlQtyList**)**
14. 🔄 **LOOP:** For each **$IteratorModel** in **$Model_AvlQtyList**
   │ 1. **Update **$IteratorModel**
      - Set **IsEnabledForFilter** = `trim($IteratorModel/Model) != ''`**
   └─ **End Loop**
15. **Commit/Save **$Category_AvlQtyList** to Database**
16. **Commit/Save **$Brand_AvlQtyList** to Database**
17. **Commit/Save **$Carrier_AvlQtyList** to Database**
18. **Commit/Save **$Capacity_AvlQtyList** to Database**
19. **Commit/Save **$Color_AvlQtyList** to Database**
20. **Commit/Save **$Grade_AvlQtyList** to Database**
21. **Commit/Save **$Model_AvlQtyList** to Database**
22. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.