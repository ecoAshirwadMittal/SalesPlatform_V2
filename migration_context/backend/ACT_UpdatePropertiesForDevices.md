# Microflow Detailed Specification: ACT_UpdatePropertiesForDevices

### 📥 Inputs (Parameters)
- **$PropertiesUtility** (Type: EcoATM_PWSMDM.PropertiesUtility)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$PropertiesUtility/UpdateBrand`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$PropertiesUtility/UpdateCapacity`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$PropertiesUtility/UpdateCarrier`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$PropertiesUtility/UpdateCategory`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **PropertiesUtility_Category_existing** via Association from **$PropertiesUtility** (Result: **$CategoryExisting**)**
                        2. **Retrieve related **PropertiesUtility_Category_New** via Association from **$PropertiesUtility** (Result: **$CategoryNew**)**
                        3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category=$CategoryExisting]` (Result: **$DeviceList_3**)**
                        4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1** in **$DeviceList_3**
                           │ 1. **Update **$IteratorDevice_1_1_1**
      - Set **Device_Category** = `$CategoryNew`**
                           └─ **End Loop**
                        5. **Commit/Save **$DeviceList_3** to Database**
                        6. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Retrieve related **PropertiesUtility_Carrier_Existing** via Association from **$PropertiesUtility** (Result: **$CarrierExisting**)**
                  2. **Retrieve related **PropertiesUtility_Carrier_new** via Association from **$PropertiesUtility** (Result: **$CarrierNew**)**
                  3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier=$CarrierExisting]` (Result: **$DeviceList_2**)**
                  4. 🔄 **LOOP:** For each **$IteratorDevice_1_1** in **$DeviceList_2**
                     │ 1. **Update **$IteratorDevice_1_1**
      - Set **Device_Carrier** = `$CarrierNew`**
                     └─ **End Loop**
                  5. **Commit/Save **$DeviceList_2** to Database**
                  6. 🔀 **DECISION:** `$PropertiesUtility/UpdateCategory`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **PropertiesUtility_Category_existing** via Association from **$PropertiesUtility** (Result: **$CategoryExisting**)**
                        2. **Retrieve related **PropertiesUtility_Category_New** via Association from **$PropertiesUtility** (Result: **$CategoryNew**)**
                        3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category=$CategoryExisting]` (Result: **$DeviceList_3**)**
                        4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1** in **$DeviceList_3**
                           │ 1. **Update **$IteratorDevice_1_1_1**
      - Set **Device_Category** = `$CategoryNew`**
                           └─ **End Loop**
                        5. **Commit/Save **$DeviceList_3** to Database**
                        6. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Retrieve related **PropertiesUtility_Capacity_Existing** via Association from **$PropertiesUtility** (Result: **$CapacityExisting**)**
            2. **Retrieve related **PropertiesUtility_Capacity_New** via Association from **$PropertiesUtility** (Result: **$CapacityNew**)**
            3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity=$CapacityExisting]` (Result: **$DeviceList_1**)**
            4. 🔄 **LOOP:** For each **$IteratorDevice_1** in **$DeviceList_1**
               │ 1. **Update **$IteratorDevice_1**
      - Set **Device_Capacity** = `$CapacityNew`**
               └─ **End Loop**
            5. **Commit/Save **$DeviceList_1** to Database**
            6. 🔀 **DECISION:** `$PropertiesUtility/UpdateCarrier`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$PropertiesUtility/UpdateCategory`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **PropertiesUtility_Category_existing** via Association from **$PropertiesUtility** (Result: **$CategoryExisting**)**
                        2. **Retrieve related **PropertiesUtility_Category_New** via Association from **$PropertiesUtility** (Result: **$CategoryNew**)**
                        3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category=$CategoryExisting]` (Result: **$DeviceList_3**)**
                        4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1** in **$DeviceList_3**
                           │ 1. **Update **$IteratorDevice_1_1_1**
      - Set **Device_Category** = `$CategoryNew`**
                           └─ **End Loop**
                        5. **Commit/Save **$DeviceList_3** to Database**
                        6. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Retrieve related **PropertiesUtility_Carrier_Existing** via Association from **$PropertiesUtility** (Result: **$CarrierExisting**)**
                  2. **Retrieve related **PropertiesUtility_Carrier_new** via Association from **$PropertiesUtility** (Result: **$CarrierNew**)**
                  3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier=$CarrierExisting]` (Result: **$DeviceList_2**)**
                  4. 🔄 **LOOP:** For each **$IteratorDevice_1_1** in **$DeviceList_2**
                     │ 1. **Update **$IteratorDevice_1_1**
      - Set **Device_Carrier** = `$CarrierNew`**
                     └─ **End Loop**
                  5. **Commit/Save **$DeviceList_2** to Database**
                  6. 🔀 **DECISION:** `$PropertiesUtility/UpdateCategory`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **PropertiesUtility_Category_existing** via Association from **$PropertiesUtility** (Result: **$CategoryExisting**)**
                        2. **Retrieve related **PropertiesUtility_Category_New** via Association from **$PropertiesUtility** (Result: **$CategoryNew**)**
                        3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category=$CategoryExisting]` (Result: **$DeviceList_3**)**
                        4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1** in **$DeviceList_3**
                           │ 1. **Update **$IteratorDevice_1_1_1**
      - Set **Device_Category** = `$CategoryNew`**
                           └─ **End Loop**
                        5. **Commit/Save **$DeviceList_3** to Database**
                        6. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **PropertiesUtility_Brand_Existing** via Association from **$PropertiesUtility** (Result: **$BrandExisting**)**
      2. **Retrieve related **PropertiesUtility_Brand_New** via Association from **$PropertiesUtility** (Result: **$BrandNew**)**
      3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Brand=$BrandExisting]` (Result: **$DeviceList**)**
      4. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList**
         │ 1. **Update **$IteratorDevice**
      - Set **Device_Brand** = `$BrandNew`**
         └─ **End Loop**
      5. **Commit/Save **$DeviceList** to Database**
      6. 🔀 **DECISION:** `$PropertiesUtility/UpdateCapacity`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$PropertiesUtility/UpdateCarrier`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$PropertiesUtility/UpdateCategory`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **PropertiesUtility_Category_existing** via Association from **$PropertiesUtility** (Result: **$CategoryExisting**)**
                        2. **Retrieve related **PropertiesUtility_Category_New** via Association from **$PropertiesUtility** (Result: **$CategoryNew**)**
                        3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category=$CategoryExisting]` (Result: **$DeviceList_3**)**
                        4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1** in **$DeviceList_3**
                           │ 1. **Update **$IteratorDevice_1_1_1**
      - Set **Device_Category** = `$CategoryNew`**
                           └─ **End Loop**
                        5. **Commit/Save **$DeviceList_3** to Database**
                        6. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Retrieve related **PropertiesUtility_Carrier_Existing** via Association from **$PropertiesUtility** (Result: **$CarrierExisting**)**
                  2. **Retrieve related **PropertiesUtility_Carrier_new** via Association from **$PropertiesUtility** (Result: **$CarrierNew**)**
                  3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier=$CarrierExisting]` (Result: **$DeviceList_2**)**
                  4. 🔄 **LOOP:** For each **$IteratorDevice_1_1** in **$DeviceList_2**
                     │ 1. **Update **$IteratorDevice_1_1**
      - Set **Device_Carrier** = `$CarrierNew`**
                     └─ **End Loop**
                  5. **Commit/Save **$DeviceList_2** to Database**
                  6. 🔀 **DECISION:** `$PropertiesUtility/UpdateCategory`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **PropertiesUtility_Category_existing** via Association from **$PropertiesUtility** (Result: **$CategoryExisting**)**
                        2. **Retrieve related **PropertiesUtility_Category_New** via Association from **$PropertiesUtility** (Result: **$CategoryNew**)**
                        3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category=$CategoryExisting]` (Result: **$DeviceList_3**)**
                        4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1** in **$DeviceList_3**
                           │ 1. **Update **$IteratorDevice_1_1_1**
      - Set **Device_Category** = `$CategoryNew`**
                           └─ **End Loop**
                        5. **Commit/Save **$DeviceList_3** to Database**
                        6. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Retrieve related **PropertiesUtility_Capacity_Existing** via Association from **$PropertiesUtility** (Result: **$CapacityExisting**)**
            2. **Retrieve related **PropertiesUtility_Capacity_New** via Association from **$PropertiesUtility** (Result: **$CapacityNew**)**
            3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity=$CapacityExisting]` (Result: **$DeviceList_1**)**
            4. 🔄 **LOOP:** For each **$IteratorDevice_1** in **$DeviceList_1**
               │ 1. **Update **$IteratorDevice_1**
      - Set **Device_Capacity** = `$CapacityNew`**
               └─ **End Loop**
            5. **Commit/Save **$DeviceList_1** to Database**
            6. 🔀 **DECISION:** `$PropertiesUtility/UpdateCarrier`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$PropertiesUtility/UpdateCategory`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **PropertiesUtility_Category_existing** via Association from **$PropertiesUtility** (Result: **$CategoryExisting**)**
                        2. **Retrieve related **PropertiesUtility_Category_New** via Association from **$PropertiesUtility** (Result: **$CategoryNew**)**
                        3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category=$CategoryExisting]` (Result: **$DeviceList_3**)**
                        4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1** in **$DeviceList_3**
                           │ 1. **Update **$IteratorDevice_1_1_1**
      - Set **Device_Category** = `$CategoryNew`**
                           └─ **End Loop**
                        5. **Commit/Save **$DeviceList_3** to Database**
                        6. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Retrieve related **PropertiesUtility_Carrier_Existing** via Association from **$PropertiesUtility** (Result: **$CarrierExisting**)**
                  2. **Retrieve related **PropertiesUtility_Carrier_new** via Association from **$PropertiesUtility** (Result: **$CarrierNew**)**
                  3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier=$CarrierExisting]` (Result: **$DeviceList_2**)**
                  4. 🔄 **LOOP:** For each **$IteratorDevice_1_1** in **$DeviceList_2**
                     │ 1. **Update **$IteratorDevice_1_1**
      - Set **Device_Carrier** = `$CarrierNew`**
                     └─ **End Loop**
                  5. **Commit/Save **$DeviceList_2** to Database**
                  6. 🔀 **DECISION:** `$PropertiesUtility/UpdateCategory`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **PropertiesUtility_Category_existing** via Association from **$PropertiesUtility** (Result: **$CategoryExisting**)**
                        2. **Retrieve related **PropertiesUtility_Category_New** via Association from **$PropertiesUtility** (Result: **$CategoryNew**)**
                        3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category=$CategoryExisting]` (Result: **$DeviceList_3**)**
                        4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1** in **$DeviceList_3**
                           │ 1. **Update **$IteratorDevice_1_1_1**
      - Set **Device_Category** = `$CategoryNew`**
                           └─ **End Loop**
                        5. **Commit/Save **$DeviceList_3** to Database**
                        6. 🔀 **DECISION:** `$PropertiesUtility/UpdateColor`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Retrieve related **PropertiesUtility_Color_Existing** via Association from **$PropertiesUtility** (Result: **$ColorExisting**)**
                              2. **Retrieve related **PropertiesUtility_Color_New** via Association from **$PropertiesUtility** (Result: **$ColorNew**)**
                              3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Color=$ColorExisting]` (Result: **$DeviceList_4**)**
                              4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1** in **$DeviceList_4**
                                 │ 1. **Update **$IteratorDevice_1_1_1_1**
      - Set **Device_Color** = `$ColorNew`**
                                 └─ **End Loop**
                              5. **Commit/Save **$DeviceList_4** to Database**
                              6. 🔀 **DECISION:** `$PropertiesUtility/UpdateGrade`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Retrieve related **PropertiesUtility_Grade_Existing** via Association from **$PropertiesUtility** (Result: **$GradeExisting**)**
                                    2. **Retrieve related **PropertiesUtility_Grade_New** via Association from **$PropertiesUtility** (Result: **$GradeNew**)**
                                    3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Grade=$GradeExisting]` (Result: **$DeviceList_5**)**
                                    4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1** in **$DeviceList_5**
                                       │ 1. **Update **$IteratorDevice_1_1_1_1_1**
      - Set **Device_Grade** = `$GradeNew`**
                                       └─ **End Loop**
                                    5. **Commit/Save **$DeviceList_5** to Database**
                                    6. 🔀 **DECISION:** `$PropertiesUtility/UpdateModel`
                                       ➔ **If [false]:**
                                          1. **Show Message (Information): `Devices Updated`**
                                          2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Retrieve related **PropertiesUtility_Model_Existing** via Association from **$PropertiesUtility** (Result: **$ModelExisting**)**
                                          2. **Retrieve related **PropertiesUtility_Model_New** via Association from **$PropertiesUtility** (Result: **$ModelNew**)**
                                          3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[EcoATM_PWSMDM.Device_Model=$ModelExisting]` (Result: **$DeviceList_6**)**
                                          4. 🔄 **LOOP:** For each **$IteratorDevice_1_1_1_1_1_1** in **$DeviceList_6**
                                             │ 1. **Update **$IteratorDevice_1_1_1_1_1_1**
      - Set **Device_Model** = `$ModelNew`**
                                             └─ **End Loop**
                                          5. **Commit/Save **$DeviceList_6** to Database**
                                          6. **Show Message (Information): `Devices Updated`**
                                          7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.