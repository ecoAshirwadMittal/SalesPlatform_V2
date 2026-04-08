# Microflow Detailed Specification: ACT_MigrateDisplayNameProperties

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSMDM.Device**  (Result: **$DeviceList**)**
2. **CreateList**
3. **CreateList**
4. **CreateList**
5. **CreateList**
6. **CreateList**
7. **CreateList**
8. **CreateList**
9. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList**
   │ 1. **Retrieve related **Device_Brand** via Association from **$IteratorDevice** (Result: **$Brand**)**
   │ 2. **Retrieve related **Device_Capacity** via Association from **$IteratorDevice** (Result: **$Capacity**)**
   │ 3. **Retrieve related **Device_Carrier** via Association from **$IteratorDevice** (Result: **$Carrier**)**
   │ 4. **Retrieve related **Device_Category** via Association from **$IteratorDevice** (Result: **$Category**)**
   │ 5. **Retrieve related **Device_Color** via Association from **$IteratorDevice** (Result: **$Color**)**
   │ 6. **Retrieve related **Device_Grade** via Association from **$IteratorDevice** (Result: **$Grade**)**
   │ 7. **Retrieve related **Device_Model** via Association from **$IteratorDevice** (Result: **$Model**)**
   │ 8. 🔀 **DECISION:** `$Brand!=empty`
   │    ➔ **If [true]:**
   │       1. **Update **$Brand**
      - Set **DisplayName** = `$Brand/Brand`**
   │       2. **Add **$$Brand
** to/from list **$BrandList****
   │       3. 🔀 **DECISION:** `$Capacity!=empty`
   │          ➔ **If [true]:**
   │             1. **Update **$Capacity**
      - Set **DisplayName** = `$Capacity/Capacity`**
   │             2. **Add **$$Capacity
** to/from list **$CapacityList****
   │             3. 🔀 **DECISION:** `$Carrier!= empty`
   │                ➔ **If [true]:**
   │                   1. **Update **$Carrier**
      - Set **DisplayName** = `$Carrier/Carrier`**
   │                   2. **Add **$$Carrier
** to/from list **$CarrierList****
   │                   3. 🔀 **DECISION:** `$Color!= empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$Color**
      - Set **DisplayName** = `$Color/Color`**
   │                         2. **Add **$$Color
** to/from list **$ColorList****
   │                         3. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$Color!= empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$Color**
      - Set **DisplayName** = `$Color/Color`**
   │                         2. **Add **$$Color
** to/from list **$ColorList****
   │                         3. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$Carrier!= empty`
   │                ➔ **If [true]:**
   │                   1. **Update **$Carrier**
      - Set **DisplayName** = `$Carrier/Carrier`**
   │                   2. **Add **$$Carrier
** to/from list **$CarrierList****
   │                   3. 🔀 **DECISION:** `$Color!= empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$Color**
      - Set **DisplayName** = `$Color/Color`**
   │                         2. **Add **$$Color
** to/from list **$ColorList****
   │                         3. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$Color!= empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$Color**
      - Set **DisplayName** = `$Color/Color`**
   │                         2. **Add **$$Color
** to/from list **$ColorList****
   │                         3. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │    ➔ **If [false]:**
   │       1. 🔀 **DECISION:** `$Capacity!=empty`
   │          ➔ **If [true]:**
   │             1. **Update **$Capacity**
      - Set **DisplayName** = `$Capacity/Capacity`**
   │             2. **Add **$$Capacity
** to/from list **$CapacityList****
   │             3. 🔀 **DECISION:** `$Carrier!= empty`
   │                ➔ **If [true]:**
   │                   1. **Update **$Carrier**
      - Set **DisplayName** = `$Carrier/Carrier`**
   │                   2. **Add **$$Carrier
** to/from list **$CarrierList****
   │                   3. 🔀 **DECISION:** `$Color!= empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$Color**
      - Set **DisplayName** = `$Color/Color`**
   │                         2. **Add **$$Color
** to/from list **$ColorList****
   │                         3. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$Color!= empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$Color**
      - Set **DisplayName** = `$Color/Color`**
   │                         2. **Add **$$Color
** to/from list **$ColorList****
   │                         3. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$Carrier!= empty`
   │                ➔ **If [true]:**
   │                   1. **Update **$Carrier**
      - Set **DisplayName** = `$Carrier/Carrier`**
   │                   2. **Add **$$Carrier
** to/from list **$CarrierList****
   │                   3. 🔀 **DECISION:** `$Color!= empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$Color**
      - Set **DisplayName** = `$Color/Color`**
   │                         2. **Add **$$Color
** to/from list **$ColorList****
   │                         3. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$Color!= empty`
   │                      ➔ **If [true]:**
   │                         1. **Update **$Color**
      - Set **DisplayName** = `$Color/Color`**
   │                         2. **Add **$$Color
** to/from list **$ColorList****
   │                         3. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$Grade!= empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$Grade**
      - Set **DisplayName** = `$Grade/Grade`**
   │                               2. **Add **$$Grade
** to/from list **$GradeList****
   │                               3. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                            ➔ **If [false]:**
   │                               1. 🔀 **DECISION:** `$Model!= empty`
   │                                  ➔ **If [true]:**
   │                                     1. **Update **$Model**
      - Set **DisplayName** = `$Model/Model`**
   │                                     2. **Add **$$Model
** to/from list **$ModeList****
   │                                     3. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   │                                  ➔ **If [false]:**
   │                                     1. 🔀 **DECISION:** `$Category!= empty`
   │                                        ➔ **If [true]:**
   │                                           1. **Update **$Category**
      - Set **DisplayName** = `$Category/Category`**
   │                                           2. **Add **$$Category
** to/from list **$CategoryList****
   │                                        ➔ **If [false]:**
   └─ **End Loop**
10. **Commit/Save **$BrandList** to Database**
11. **Commit/Save **$CapacityList** to Database**
12. **Commit/Save **$CarrierList** to Database**
13. **Commit/Save **$CategoryList** to Database**
14. **Commit/Save **$ColorList** to Database**
15. **Commit/Save **$ModeList** to Database**
16. **Commit/Save **$GradeList** to Database**
17. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.