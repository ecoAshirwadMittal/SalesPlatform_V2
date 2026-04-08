# Microflow Detailed Specification: VAL_PWSConstants

### 📥 Inputs (Parameters)
- **$PWSConstants** (Type: EcoATM_PWS.PWSConstants)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$isValid** = `true`**
2. 🔀 **DECISION:** `$PWSConstants/SLADays!= empty and $PWSConstants/SLADays > 0`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$PWSConstants/SalesEmail!=empty and isMatch($PWSConstants/SalesEmail, '[A-Za-z0-9!#$%&''*+/=?^_`{|}~-]+(?:\.[A-Za-z0-9!#$%&''*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?')`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$PWSConstants/SendFirstReminder`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. **Update Variable **$isValid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$PWSConstants/SendFirstReminder`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. **Update Variable **$isValid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. **Update Variable **$isValid** = `false`**
      2. **ValidationFeedback**
      3. 🔀 **DECISION:** `$PWSConstants/SalesEmail!=empty and isMatch($PWSConstants/SalesEmail, '[A-Za-z0-9!#$%&''*+/=?^_`{|}~-]+(?:\.[A-Za-z0-9!#$%&''*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?')`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$PWSConstants/SendFirstReminder`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. **Update Variable **$isValid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$PWSConstants/SendFirstReminder`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. **Update Variable **$isValid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$PWSConstants/SendSecondReminder =true and $PWSConstants/SendFirstReminder = true and $PWSConstants/HoursSecondCounterReminderEmail!=empty and $PWSConstants/HoursSecondCounterReminderEmail>0 and $PWSConstants/HoursFirstCounterReminderEmail!=empty and $PWSConstants/HoursFirstCounterReminderEmail>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$PWSConstants/HoursSecondCounterReminderEmail> $PWSConstants/HoursFirstCounterReminderEmail`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.