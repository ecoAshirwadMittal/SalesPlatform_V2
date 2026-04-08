# Microflow Detailed Specification: VAL_BatchDetails

### 📥 Inputs (Parameters)
- **$IncomingEmailConfiguration** (Type: Email_Connector.IncomingEmailConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$BatchDetailsComplete** = `true`**
2. 🔀 **DECISION:** `$IncomingEmailConfiguration/Folder!= empty and $IncomingEmailConfiguration/Folder!= ''`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Handling!= empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Handling = Email_Connector.ENUM_MessageHandling.NoHandling and $IncomingEmailConfiguration/UseBatchImport = true`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/Handling = Email_Connector.ENUM_MessageHandling.MoveMessage then $IncomingEmailConfiguration/MoveFolder!= empty and $IncomingEmailConfiguration/MoveFolder!= '' else true`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$BatchDetailsComplete** = `false`**
                        3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
               ➔ **If [true]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$BatchDetailsComplete** = `false`**
                  3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/Handling = Email_Connector.ENUM_MessageHandling.MoveMessage then $IncomingEmailConfiguration/MoveFolder!= empty and $IncomingEmailConfiguration/MoveFolder!= '' else true`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$BatchDetailsComplete** = `false`**
                        3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. **Update Variable **$BatchDetailsComplete** = `false`**
            3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/Handling = Email_Connector.ENUM_MessageHandling.MoveMessage then $IncomingEmailConfiguration/MoveFolder!= empty and $IncomingEmailConfiguration/MoveFolder!= '' else true`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$BatchDetailsComplete** = `false`**
                        3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$BatchDetailsComplete** = `false`**
                  3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$BatchDetailsComplete** = `false`**
                        3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. **Update Variable **$BatchDetailsComplete** = `false`**
      3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Handling!= empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Handling = Email_Connector.ENUM_MessageHandling.NoHandling and $IncomingEmailConfiguration/UseBatchImport = true`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/Handling = Email_Connector.ENUM_MessageHandling.MoveMessage then $IncomingEmailConfiguration/MoveFolder!= empty and $IncomingEmailConfiguration/MoveFolder!= '' else true`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$BatchDetailsComplete** = `false`**
                        3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
               ➔ **If [true]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$BatchDetailsComplete** = `false`**
                  3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/Handling = Email_Connector.ENUM_MessageHandling.MoveMessage then $IncomingEmailConfiguration/MoveFolder!= empty and $IncomingEmailConfiguration/MoveFolder!= '' else true`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$BatchDetailsComplete** = `false`**
                        3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                             ➔ **If [true]:**
                                                1. 🏁 **END:** Return `$BatchDetailsComplete`
                                             ➔ **If [false]:**
                                                1. **ValidationFeedback**
                                                2. **Update Variable **$BatchDetailsComplete** = `false`**
                                                3. 🏁 **END:** Return `$BatchDetailsComplete`
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. **Update Variable **$BatchDetailsComplete** = `false`**
            3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/Handling = Email_Connector.ENUM_MessageHandling.MoveMessage then $IncomingEmailConfiguration/MoveFolder!= empty and $IncomingEmailConfiguration/MoveFolder!= '' else true`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$BatchDetailsComplete** = `false`**
                        3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. **Update Variable **$BatchDetailsComplete** = `false`**
                  3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/MoveFolder != $IncomingEmailConfiguration/Folder then true else false`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. **Update Variable **$BatchDetailsComplete** = `false`**
                        3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. **Update Variable **$BatchDetailsComplete** = `false`**
                              3. 🔀 **DECISION:** `if $IncomingEmailConfiguration/UseBatchImport = false then $IncomingEmailConfiguration/BatchSize!= empty and $IncomingEmailConfiguration/BatchSize > 0 else true`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`
                                 ➔ **If [false]:**
                                    1. **ValidationFeedback**
                                    2. **Update Variable **$BatchDetailsComplete** = `false`**
                                    3. 🔀 **DECISION:** `$IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout!= empty and $IncomingEmailConfiguration/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.EmailAccount/Timeout> 0`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$BatchDetailsComplete`
                                       ➔ **If [false]:**
                                          1. **ValidationFeedback**
                                          2. **Update Variable **$BatchDetailsComplete** = `false`**
                                          3. 🏁 **END:** Return `$BatchDetailsComplete`

**Final Result:** This process concludes by returning a [Boolean] value.