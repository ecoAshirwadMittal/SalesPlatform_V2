# Microflow Detailed Specification: Val_Authentication

### 📥 Inputs (Parameters)
- **$Authentication** (Type: MicrosoftGraph.Authentication)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Valid** = `true`**
2. 🔀 **DECISION:** `$Authentication != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `Check if "$Authentication/AppId" is not empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `Check if "$Authentication/Tenant_Id" is not empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Authentication/Authority != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
               ➔ **If [false]:**
                  1. **Update Variable **$Valid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$Authentication/Authority != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
         ➔ **If [false]:**
            1. **Update Variable **$Valid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `Check if "$Authentication/Tenant_Id" is not empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Authentication/Authority != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
               ➔ **If [false]:**
                  1. **Update Variable **$Valid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$Authentication/Authority != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
   ➔ **If [false]:**
      1. **Update Variable **$Valid** = `false`**
      2. 🔀 **DECISION:** `Check if "$Authentication/AppId" is not empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `Check if "$Authentication/Tenant_Id" is not empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Authentication/Authority != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
               ➔ **If [false]:**
                  1. **Update Variable **$Valid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$Authentication/Authority != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
         ➔ **If [false]:**
            1. **Update Variable **$Valid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `Check if "$Authentication/Tenant_Id" is not empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$Authentication/Authority != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
               ➔ **If [false]:**
                  1. **Update Variable **$Valid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$Authentication/Authority != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `Check if "$Authentication/client_secret" is not empty`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$Authentication/Prompt != empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseMode != empty`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$Authentication/MicrosoftGraph.SelectedResponseType != empty`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`

**Final Result:** This process concludes by returning a [Boolean] value.