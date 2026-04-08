# Microflow Detailed Specification: VAL_EmailTemplateRecipients

### 📥 Inputs (Parameters)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Valid** = `true`**
2. 🔀 **DECISION:** `$EmailTemplate/FromAddress!= empty and trim($EmailTemplate/FromAddress) != ''`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `isMatch($EmailTemplate/FromAddress, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$EmailTemplate/To!= empty and trim($EmailTemplate/To) != ''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `isMatch($EmailTemplate/To, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$EmailTemplate/ReplyTo != empty and trim($EmailTemplate/ReplyTo) != ''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/ReplyTo, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/ReplyTo != empty and trim($EmailTemplate/ReplyTo) != ''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/ReplyTo, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$EmailTemplate/ReplyTo != empty and trim($EmailTemplate/ReplyTo) != ''`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `isMatch($EmailTemplate/ReplyTo, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
         ➔ **If [false]:**
            1. **Update Variable **$Valid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$EmailTemplate/To!= empty and trim($EmailTemplate/To) != ''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `isMatch($EmailTemplate/To, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$EmailTemplate/ReplyTo != empty and trim($EmailTemplate/ReplyTo) != ''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/ReplyTo, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/ReplyTo != empty and trim($EmailTemplate/ReplyTo) != ''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/ReplyTo, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                         ➔ **If [false]:**
                                                            1. **Update Variable **$Valid** = `false`**
                                                            2. **ValidationFeedback**
                                                            3. 🏁 **END:** Return `$Valid`
                                                         ➔ **If [true]:**
                                                            1. 🏁 **END:** Return `$Valid`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$EmailTemplate/ReplyTo != empty and trim($EmailTemplate/ReplyTo) != ''`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `isMatch($EmailTemplate/ReplyTo, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$EmailTemplate/To!= empty and trim($EmailTemplate/To) != ''`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `isMatch($EmailTemplate/To, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
               ➔ **If [false]:**
                  1. **Update Variable **$Valid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$EmailTemplate/ReplyTo != empty and trim($EmailTemplate/ReplyTo) != ''`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `isMatch($EmailTemplate/ReplyTo, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$EmailTemplate/ReplyTo != empty and trim($EmailTemplate/ReplyTo) != ''`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `isMatch($EmailTemplate/ReplyTo, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                                   ➔ **If [false]:**
                                                      1. **Update Variable **$Valid** = `false`**
                                                      2. **ValidationFeedback**
                                                      3. 🏁 **END:** Return `$Valid`
                                                   ➔ **If [true]:**
                                                      1. 🏁 **END:** Return `$Valid`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$EmailTemplate/ReplyTo != empty and trim($EmailTemplate/ReplyTo) != ''`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$Valid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **ValidationFeedback**
                              3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$Valid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `isMatch($EmailTemplate/ReplyTo, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/CC!= empty and trim($EmailTemplate/CC) != ''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$Valid`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `isMatch($EmailTemplate/CC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/BCC!= empty and trim($EmailTemplate/BCC) != ''`
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return `$Valid`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `isMatch($EmailTemplate/BCC, getCaption(Email_Connector.ENUM_RecipientsRegEx.regex))`
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **ValidationFeedback**
                                                3. 🏁 **END:** Return `$Valid`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$Valid`

**Final Result:** This process concludes by returning a [Boolean] value.