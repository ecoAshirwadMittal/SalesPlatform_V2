# Microflow Detailed Specification: ACT_EmailTemplate_Save

### 📥 Inputs (Parameters)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Email_Connector.VAL_EmailTemplateRecipients** (Result: **$Valid**)**
2. 🔀 **DECISION:** `$EmailTemplate/TemplateName != empty and trim($EmailTemplate/TemplateName) != ''`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `($EmailTemplate/UseOnlyPlainText = false and ($EmailTemplate/Content != empty or trim($EmailTemplate/Content) != '')) or true`
               ➔ **If [false]:**
                  1. **Update Variable **$Valid** = `false`**
                  2. **Show Message (Information): `HTML email body cannot be empty.`**
                  3. **ValidationFeedback**
                  4. 🔀 **DECISION:** `$EmailTemplate/PlainBody != empty or $EmailTemplate/PlainBody !=''`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **Show Message (Information): `Plain email body cannot be empty.`**
                              3. **ValidationFeedback**
                              4. **JavaCallAction**
                              5. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                          3. **ValidationFeedback**
                                          4. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **JavaCallAction**
                              2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                          3. **ValidationFeedback**
                                          4. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **JavaCallAction**
                        2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Valid`
                                 ➔ **If [true]:**
                                    1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                    2. **Commit/Save **$TokenList** to Database**
                                    3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                    4. **Commit/Save **$AttachmentList** to Database**
                                    5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                    6. **Commit/Save **$EmailTemplate** to Database**
                                    7. **Close current page/popup**
                                    8. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                    3. **ValidationFeedback**
                                    4. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **JavaCallAction**
                  2. 🔀 **DECISION:** `length(trim($PlainString))>0`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/PlainBody != empty or $EmailTemplate/PlainBody !=''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **Show Message (Information): `Plain email body cannot be empty.`**
                                    3. **ValidationFeedback**
                                    4. **JavaCallAction**
                                    5. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                                3. **ValidationFeedback**
                                                4. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **JavaCallAction**
                                    2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                                3. **ValidationFeedback**
                                                4. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **JavaCallAction**
                              2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                          3. **ValidationFeedback**
                                          4. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **Show Message (Information): `HTML email body cannot contains only spaces or newline characters.`**
                        3. **ValidationFeedback**
                        4. 🔀 **DECISION:** `$EmailTemplate/PlainBody != empty or $EmailTemplate/PlainBody !=''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **Show Message (Information): `Plain email body cannot be empty.`**
                                    3. **ValidationFeedback**
                                    4. **JavaCallAction**
                                    5. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                                3. **ValidationFeedback**
                                                4. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **JavaCallAction**
                                    2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                                3. **ValidationFeedback**
                                                4. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **JavaCallAction**
                              2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                          3. **ValidationFeedback**
                                          4. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$EmailTemplate/PlainBody != empty or $EmailTemplate/PlainBody !=''`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **Show Message (Information): `Plain email body cannot be empty.`**
                        3. **ValidationFeedback**
                        4. **JavaCallAction**
                        5. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Valid`
                                 ➔ **If [true]:**
                                    1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                    2. **Commit/Save **$TokenList** to Database**
                                    3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                    4. **Commit/Save **$AttachmentList** to Database**
                                    5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                    6. **Commit/Save **$EmailTemplate** to Database**
                                    7. **Close current page/popup**
                                    8. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                    3. **ValidationFeedback**
                                    4. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **JavaCallAction**
                        2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Valid`
                                 ➔ **If [true]:**
                                    1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                    2. **Commit/Save **$TokenList** to Database**
                                    3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                    4. **Commit/Save **$AttachmentList** to Database**
                                    5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                    6. **Commit/Save **$EmailTemplate** to Database**
                                    7. **Close current page/popup**
                                    8. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                    3. **ValidationFeedback**
                                    4. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **JavaCallAction**
                  2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                              2. **Commit/Save **$TokenList** to Database**
                              3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                              4. **Commit/Save **$AttachmentList** to Database**
                              5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                              6. **Commit/Save **$EmailTemplate** to Database**
                              7. **Close current page/popup**
                              8. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Valid`
                                 ➔ **If [true]:**
                                    1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                    2. **Commit/Save **$TokenList** to Database**
                                    3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                    4. **Commit/Save **$AttachmentList** to Database**
                                    5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                    6. **Commit/Save **$EmailTemplate** to Database**
                                    7. **Close current page/popup**
                                    8. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                              3. **ValidationFeedback**
                              4. 🔀 **DECISION:** `$Valid`
                                 ➔ **If [true]:**
                                    1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                    2. **Commit/Save **$TokenList** to Database**
                                    3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                    4. **Commit/Save **$AttachmentList** to Database**
                                    5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                    6. **Commit/Save **$EmailTemplate** to Database**
                                    7. **Close current page/popup**
                                    8. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update Variable **$Valid** = `false`**
      2. **ValidationFeedback**
      3. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `($EmailTemplate/UseOnlyPlainText = false and ($EmailTemplate/Content != empty or trim($EmailTemplate/Content) != '')) or true`
               ➔ **If [false]:**
                  1. **Update Variable **$Valid** = `false`**
                  2. **Show Message (Information): `HTML email body cannot be empty.`**
                  3. **ValidationFeedback**
                  4. 🔀 **DECISION:** `$EmailTemplate/PlainBody != empty or $EmailTemplate/PlainBody !=''`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **Show Message (Information): `Plain email body cannot be empty.`**
                              3. **ValidationFeedback**
                              4. **JavaCallAction**
                              5. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                          3. **ValidationFeedback**
                                          4. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **JavaCallAction**
                              2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                          3. **ValidationFeedback**
                                          4. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **JavaCallAction**
                        2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Valid`
                                 ➔ **If [true]:**
                                    1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                    2. **Commit/Save **$TokenList** to Database**
                                    3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                    4. **Commit/Save **$AttachmentList** to Database**
                                    5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                    6. **Commit/Save **$EmailTemplate** to Database**
                                    7. **Close current page/popup**
                                    8. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                    3. **ValidationFeedback**
                                    4. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **JavaCallAction**
                  2. 🔀 **DECISION:** `length(trim($PlainString))>0`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/PlainBody != empty or $EmailTemplate/PlainBody !=''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **Show Message (Information): `Plain email body cannot be empty.`**
                                    3. **ValidationFeedback**
                                    4. **JavaCallAction**
                                    5. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                                3. **ValidationFeedback**
                                                4. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **JavaCallAction**
                                    2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                                3. **ValidationFeedback**
                                                4. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **JavaCallAction**
                              2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                          3. **ValidationFeedback**
                                          4. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **Show Message (Information): `HTML email body cannot contains only spaces or newline characters.`**
                        3. **ValidationFeedback**
                        4. 🔀 **DECISION:** `$EmailTemplate/PlainBody != empty or $EmailTemplate/PlainBody !=''`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **Show Message (Information): `Plain email body cannot be empty.`**
                                    3. **ValidationFeedback**
                                    4. **JavaCallAction**
                                    5. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                                3. **ValidationFeedback**
                                                4. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **JavaCallAction**
                                    2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Update Variable **$Valid** = `false`**
                                                2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                                3. **ValidationFeedback**
                                                4. 🔀 **DECISION:** `$Valid`
                                                   ➔ **If [true]:**
                                                      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                      2. **Commit/Save **$TokenList** to Database**
                                                      3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                      4. **Commit/Save **$AttachmentList** to Database**
                                                      5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                      6. **Commit/Save **$EmailTemplate** to Database**
                                                      7. **Close current page/popup**
                                                      8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **JavaCallAction**
                              2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **Update Variable **$Valid** = `false`**
                                          2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                          3. **ValidationFeedback**
                                          4. 🔀 **DECISION:** `$Valid`
                                             ➔ **If [true]:**
                                                1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                                2. **Commit/Save **$TokenList** to Database**
                                                3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                                4. **Commit/Save **$AttachmentList** to Database**
                                                5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                                6. **Commit/Save **$EmailTemplate** to Database**
                                                7. **Close current page/popup**
                                                8. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$EmailTemplate/PlainBody != empty or $EmailTemplate/PlainBody !=''`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                     ➔ **If [false]:**
                        1. **Update Variable **$Valid** = `false`**
                        2. **Show Message (Information): `Plain email body cannot be empty.`**
                        3. **ValidationFeedback**
                        4. **JavaCallAction**
                        5. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Valid`
                                 ➔ **If [true]:**
                                    1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                    2. **Commit/Save **$TokenList** to Database**
                                    3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                    4. **Commit/Save **$AttachmentList** to Database**
                                    5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                    6. **Commit/Save **$EmailTemplate** to Database**
                                    7. **Close current page/popup**
                                    8. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                    3. **ValidationFeedback**
                                    4. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **JavaCallAction**
                        2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Valid`
                                 ➔ **If [true]:**
                                    1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                    2. **Commit/Save **$TokenList** to Database**
                                    3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                    4. **Commit/Save **$AttachmentList** to Database**
                                    5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                    6. **Commit/Save **$EmailTemplate** to Database**
                                    7. **Close current page/popup**
                                    8. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. **Update Variable **$Valid** = `false`**
                                    2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                                    3. **ValidationFeedback**
                                    4. 🔀 **DECISION:** `$Valid`
                                       ➔ **If [true]:**
                                          1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                          2. **Commit/Save **$TokenList** to Database**
                                          3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                          4. **Commit/Save **$AttachmentList** to Database**
                                          5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                          6. **Commit/Save **$EmailTemplate** to Database**
                                          7. **Close current page/popup**
                                          8. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **JavaCallAction**
                  2. 🔀 **DECISION:** `length(trim($ParsedPlainString))>0`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Valid`
                           ➔ **If [true]:**
                              1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                              2. **Commit/Save **$TokenList** to Database**
                              3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                              4. **Commit/Save **$AttachmentList** to Database**
                              5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                              6. **Commit/Save **$EmailTemplate** to Database**
                              7. **Close current page/popup**
                              8. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$EmailTemplate/UseOnlyPlainText`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$Valid`
                                 ➔ **If [true]:**
                                    1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                    2. **Commit/Save **$TokenList** to Database**
                                    3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                    4. **Commit/Save **$AttachmentList** to Database**
                                    5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                    6. **Commit/Save **$EmailTemplate** to Database**
                                    7. **Close current page/popup**
                                    8. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Update Variable **$Valid** = `false`**
                              2. **Show Message (Information): `Plain email body cannot contain only spaces or newline characters.`**
                              3. **ValidationFeedback**
                              4. 🔀 **DECISION:** `$Valid`
                                 ➔ **If [true]:**
                                    1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
                                    2. **Commit/Save **$TokenList** to Database**
                                    3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
                                    4. **Commit/Save **$AttachmentList** to Database**
                                    5. **Update **$EmailTemplate**
      - Set **hasAttachment** = `$AttachmentList!=empty`**
                                    6. **Commit/Save **$EmailTemplate** to Database**
                                    7. **Close current page/popup**
                                    8. 🏁 **END:** Return empty
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.