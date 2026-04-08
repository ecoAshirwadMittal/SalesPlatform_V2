# Microflow Detailed Specification: IVK_Column_Save

### 📥 Inputs (Parameters)
- **$Column** (Type: ExcelImporter.Column)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Valid** = `true`**
2. 🔀 **DECISION:** `$Column/DataSource`
   ➔ **If [CellValue]:**
      1. 🔀 **DECISION:** `$Column/ColNumber != empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$Column/Text != empty and $Column/Text != ''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Column/MappingType`
                     ➔ **If [(empty)]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [Attribute]:**
                        1. 🔀 **DECISION:** `$Column/IsKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [Reference]:**
                        1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                     ➔ **If [DoNotUse]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Valid** = `false`**
                  3. 🔀 **DECISION:** `$Column/MappingType`
                     ➔ **If [(empty)]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [Attribute]:**
                        1. 🔀 **DECISION:** `$Column/IsKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [Reference]:**
                        1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                     ➔ **If [DoNotUse]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Valid** = `false`**
            3. 🔀 **DECISION:** `$Column/Text != empty and $Column/Text != ''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Column/MappingType`
                     ➔ **If [(empty)]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [Attribute]:**
                        1. 🔀 **DECISION:** `$Column/IsKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [Reference]:**
                        1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                     ➔ **If [DoNotUse]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Valid** = `false`**
                  3. 🔀 **DECISION:** `$Column/MappingType`
                     ➔ **If [(empty)]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [Attribute]:**
                        1. 🔀 **DECISION:** `$Column/IsKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [Reference]:**
                        1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                     ➔ **If [DoNotUse]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
   ➔ **If [(empty)]:**
      1. **ValidationFeedback**
      2. **Update Variable **$Valid** = `false`**
      3. 🔀 **DECISION:** `$Column/ColNumber != empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$Column/Text != empty and $Column/Text != ''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Column/MappingType`
                     ➔ **If [(empty)]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [Attribute]:**
                        1. 🔀 **DECISION:** `$Column/IsKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [Reference]:**
                        1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                     ➔ **If [DoNotUse]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Valid** = `false`**
                  3. 🔀 **DECISION:** `$Column/MappingType`
                     ➔ **If [(empty)]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [Attribute]:**
                        1. 🔀 **DECISION:** `$Column/IsKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [Reference]:**
                        1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                     ➔ **If [DoNotUse]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Valid** = `false`**
            3. 🔀 **DECISION:** `$Column/Text != empty and $Column/Text != ''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Column/MappingType`
                     ➔ **If [(empty)]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [Attribute]:**
                        1. 🔀 **DECISION:** `$Column/IsKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [Reference]:**
                        1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                     ➔ **If [DoNotUse]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Valid** = `false`**
                  3. 🔀 **DECISION:** `$Column/MappingType`
                     ➔ **If [(empty)]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [Attribute]:**
                        1. 🔀 **DECISION:** `$Column/IsKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [Reference]:**
                        1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$Valid** = `false`**
                                          3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$Valid** = `false`**
                                                3. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                                      2. 🔀 **DECISION:** `$commitResult`
                                                         ➔ **If [false]:**
                                                            1. 🏁 **END:** Return empty
                                                         ➔ **If [true]:**
                                                            1. **Update **$Column****
                                                            2. **Close current page/popup**
                                                            3. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                     ➔ **If [DoNotUse]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
   ➔ **If [DocumentPropertyRowNr]:**
      1. **Update **$Column**
      - Set **Text** = `getCaption( $Column/DataSource )`**
      2. **Update **$Column**
      - Set **IsKey** = `ExcelImporter.YesNo.No`
      - Set **IsReferenceKey** = `ExcelImporter.ReferenceKeyType.NoKey`
      - Set **ColNumber** = `999`**
      3. 🔀 **DECISION:** `$Column/MappingType`
         ➔ **If [(empty)]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Valid** = `false`**
            3. 🔀 **DECISION:** `$Valid`
               ➔ **If [true]:**
                  1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                  2. 🔀 **DECISION:** `$commitResult`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$Column****
                        2. **Close current page/popup**
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [Attribute]:**
            1. 🔀 **DECISION:** `$Column/IsKey != empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Valid** = `false`**
                  3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
         ➔ **If [Reference]:**
            1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Valid** = `false`**
                  3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
         ➔ **If [DoNotUse]:**
            1. 🔀 **DECISION:** `$Valid`
               ➔ **If [true]:**
                  1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                  2. 🔀 **DECISION:** `$commitResult`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$Column****
                        2. **Close current page/popup**
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
   ➔ **If [DocumentPropertySheetNr]:**
      1. **Update **$Column**
      - Set **Text** = `getCaption( $Column/DataSource )`**
      2. **Update **$Column**
      - Set **IsKey** = `ExcelImporter.YesNo.No`
      - Set **IsReferenceKey** = `ExcelImporter.ReferenceKeyType.NoKey`
      - Set **ColNumber** = `999`**
      3. 🔀 **DECISION:** `$Column/MappingType`
         ➔ **If [(empty)]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Valid** = `false`**
            3. 🔀 **DECISION:** `$Valid`
               ➔ **If [true]:**
                  1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                  2. 🔀 **DECISION:** `$commitResult`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$Column****
                        2. **Close current page/popup**
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [Attribute]:**
            1. 🔀 **DECISION:** `$Column/IsKey != empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Valid** = `false`**
                  3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
         ➔ **If [Reference]:**
            1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Valid** = `false`**
                  3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
         ➔ **If [DoNotUse]:**
            1. 🔀 **DECISION:** `$Valid`
               ➔ **If [true]:**
                  1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                  2. 🔀 **DECISION:** `$commitResult`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$Column****
                        2. **Close current page/popup**
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
   ➔ **If [StaticValue]:**
      1. **Update **$Column**
      - Set **IsKey** = `ExcelImporter.YesNo.No`
      - Set **IsReferenceKey** = `ExcelImporter.ReferenceKeyType.NoKey`
      - Set **ColNumber** = `999`**
      2. 🔀 **DECISION:** `$Column/MappingType`
         ➔ **If [(empty)]:**
            1. **ValidationFeedback**
            2. **Update Variable **$Valid** = `false`**
            3. 🔀 **DECISION:** `$Valid`
               ➔ **If [true]:**
                  1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                  2. 🔀 **DECISION:** `$commitResult`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$Column****
                        2. **Close current page/popup**
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [Attribute]:**
            1. 🔀 **DECISION:** `$Column/IsKey != empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Valid** = `false`**
                  3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                              2. 🔀 **DECISION:** `$commitResult`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$Column****
                                    2. **Close current page/popup**
                                    3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
         ➔ **If [Reference]:**
            1. 🔀 **DECISION:** `$Column/IsReferenceKey != empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$Valid** = `false`**
                  3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectMember_Reference != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$Valid** = `false`**
                        3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectReference != empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$Valid** = `false`**
                              3. 🔀 **DECISION:** `$Column/ExcelImporter.Column_MxObjectType_Reference != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$Valid** = `false`**
                                    3. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                                          2. 🔀 **DECISION:** `$commitResult`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$Column****
                                                2. **Close current page/popup**
                                                3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
         ➔ **If [DoNotUse]:**
            1. 🔀 **DECISION:** `$Valid`
               ➔ **If [true]:**
                  1. **Call Microflow **ExcelImporter.BCo_Column** (Result: **$commitResult**)**
                  2. 🔀 **DECISION:** `$commitResult`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$Column****
                        2. **Close current page/popup**
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.