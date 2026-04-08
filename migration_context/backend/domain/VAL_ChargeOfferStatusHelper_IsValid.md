# Microflow Detailed Specification: VAL_ChargeOfferStatusHelper_IsValid

### 📥 Inputs (Parameters)
- **$ChangeOfferStatusHelper** (Type: EcoATM_PWS.ChangeOfferStatusHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$isValid** = `true`**
2. 🔀 **DECISION:** `$ChangeOfferStatusHelper/AllPeriod`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/EcoATM_PWS.ChangeOfferStatusHelper_Order!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `($ChangeOfferStatusHelper/NotOrderStatusChange)`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/EcoATM_PWS.ChangeOfferStatusHelper_OrderStatus!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `($ChangeOfferStatusHelper/NotOrderStatusChange)`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/EcoATM_PWS.ChangeOfferStatusHelper_OrderStatus!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/StartingDate!=empty`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `($ChangeOfferStatusHelper/NotOrderStatusChange)`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/EcoATM_PWS.ChangeOfferStatusHelper_OrderStatus!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$isValid`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$isValid`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/EndingDate!=empty`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `($ChangeOfferStatusHelper/NotOrderStatusChange)`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/EcoATM_PWS.ChangeOfferStatusHelper_OrderStatus!=empty`
                           ➔ **If [false]:**
                              1. **Update Variable **$isValid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$isValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$isValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/EndingDate>$ChangeOfferStatusHelper/StartingDate`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `($ChangeOfferStatusHelper/NotOrderStatusChange)`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/EcoATM_PWS.ChangeOfferStatusHelper_OrderStatus!=empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `($ChangeOfferStatusHelper/NotOrderStatusChange)`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/EcoATM_PWS.ChangeOfferStatusHelper_OrderStatus!=empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$isValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `not($ChangeOfferStatusHelper/AllPeriod)`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/FromOfferStatus!=empty`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$isValid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$isValid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$isValid`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$isValid`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.